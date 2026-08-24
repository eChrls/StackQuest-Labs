<script setup lang="ts">
import { onMounted } from 'vue';
import { useHome } from './composables/useHome';

const { data, loading, error, load } = useHome();
onMounted(load);
</script>

<template>
  <div v-if="loading" class="state-message">Loading…</div>
  <div v-else-if="error" class="state-message">Could not load the homepage: {{ error }}</div>

  <div v-else-if="data" class="site">
    <div class="topbar">
      <div class="brand">{{ data.site.name }}</div>
      <div class="nav">
        <a v-for="link in data.nav" :key="link.href" :href="link.href">{{ link.label }}</a>
      </div>
    </div>

    <div class="hero">
      <img class="hero-image" :src="data.hero.image" />
      <div class="hero-copy">
        <p class="eyebrow">{{ data.hero.eyebrow }}</p>
        <h1>{{ data.hero.headline }}</h1>
        <p class="subhead">{{ data.hero.subheadline }}</p>
        <a class="cta" :href="data.hero.ctaHref">{{ data.hero.ctaLabel }}</a>
      </div>
    </div>

    <div class="benefits">
      <div v-for="benefit in data.benefits" :key="benefit.id" class="benefit-card">
        <h3>{{ benefit.title }}</h3>
        <p>{{ benefit.body }}</p>
      </div>
    </div>

    <div class="testimonial">
      <p>“{{ data.testimonial.quote }}”</p>
      <p class="testimonial-author">{{ data.testimonial.author }}</p>
    </div>

    <div class="footer">
      <div v-for="col in data.footer.columns" :key="col.title" class="footer-col">
        <h4>{{ col.title }}</h4>
        <a v-for="link in col.links" :key="link.href" :href="link.href">{{ link.label }}</a>
      </div>
      <p class="footer-note">{{ data.footer.note }}</p>
    </div>
  </div>
</template>
