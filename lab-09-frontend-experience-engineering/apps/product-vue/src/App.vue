<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useProduct } from './composables/useProduct';

const { product, related, category, loading, error, load } = useProduct();
onMounted(load);

const selectedVariantId = ref<string | null>(null);

function selectVariant(id: string) {
  selectedVariantId.value = id;
}

const effectivePrice = computed(() => {
  if (!product.value) return 0;
  const variants = product.value.variants;
  if (!variants) return product.value.price;
  const selected = variants.options.find((opt) => opt.id === selectedVariantId.value) ?? variants.options[0];
  return product.value.price + (selected?.priceDelta ?? 0);
});

const mainImage = computed(() => product.value?.images[0] ?? '');
</script>

<template>
  <div v-if="loading" class="state-message">Loading…</div>
  <div v-else-if="error" class="state-message">Could not load this product: {{ error }}</div>

  <div v-else-if="product" class="product-page">
    <nav aria-label="Breadcrumb" class="breadcrumbs">
      <ol>
        <li><a href="/">Home</a></li>
        <li v-if="category"><a :href="`/catalog?category=${category.id}`">{{ category.name }}</a></li>
        <li aria-current="page">{{ product.name }}</li>
      </ol>
    </nav>

    <div class="product-main">
      <div class="gallery">
        <img class="gallery-main" :src="mainImage" :alt="product.name" />
        <div class="gallery-thumbs">
          <img v-for="(img, i) in product.images" :key="img" :src="img" :alt="`${product.name} view ${i + 1}`" />
        </div>
      </div>

      <div class="product-info">
        <h1>{{ product.name }}</h1>
        <p class="price">{{ effectivePrice }} {{ product.currency }}</p>
        <p class="availability" :class="product.availability">
          {{ product.availability.replace('_', ' ') }}
        </p>
        <p class="short-description">{{ product.shortDescription }}</p>

        <div v-if="product.variants" class="variants">
          <p class="variants-label">{{ product.variants.label }}</p>
          <div class="swatches">
            <span
              v-for="opt in product.variants.options"
              :key="opt.id"
              class="swatch"
              :class="{ selected: (selectedVariantId ?? product.variants.options[0].id) === opt.id }"
              :style="opt.swatch ? { backgroundColor: opt.swatch } : {}"
              :title="opt.label"
              @click="selectVariant(opt.id)"
            >
              <span v-if="!opt.swatch" class="swatch-text">{{ opt.label }}</span>
            </span>
          </div>
        </div>

        <button type="button" class="cta" :disabled="product.availability === 'out_of_stock'">
          {{ product.availability === 'out_of_stock' ? 'Out of stock' : 'Add to cart' }}
        </button>

        <p class="description">{{ product.description }}</p>

        <dl class="specs">
          <template v-for="spec in product.specs" :key="spec.label">
            <dt>{{ spec.label }}</dt>
            <dd>{{ spec.value }}</dd>
          </template>
        </dl>
      </div>
    </div>

    <div v-if="related.length" class="related">
      <h2>You may also like</h2>
      <div class="related-grid">
        <div v-for="item in related" :key="item.id" class="related-card">
          <img :src="item.images[0]" :alt="item.name" />
          <p class="related-name">{{ item.name }}</p>
          <p class="related-price">{{ item.price }} {{ item.currency }}</p>
        </div>
      </div>
    </div>
  </div>
</template>
