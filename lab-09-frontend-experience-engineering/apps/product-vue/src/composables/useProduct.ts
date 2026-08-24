import { ref } from 'vue';
import type { Category, Product } from '../types';

const FEATURED_PRODUCT_ID = 'aurora-desk-lamp';

export function useProduct() {
  const product = ref<Product | null>(null);
  const related = ref<Product[]>([]);
  const category = ref<Category | null>(null);
  const loading = ref(true);
  const error = ref<string | null>(null);

  async function load() {
    loading.value = true;
    error.value = null;
    try {
      const [productRes, allProductsRes, categoriesRes] = await Promise.all([
        fetch(`/api/products/${FEATURED_PRODUCT_ID}`),
        fetch('/api/products'),
        fetch('/api/categories'),
      ]);
      if (!productRes.ok) throw new Error(`CMS responded with ${productRes.status}`);

      const loadedProduct = (await productRes.json()) as Product;
      const allProducts = (await allProductsRes.json()) as Product[];
      const categories = (await categoriesRes.json()) as Category[];

      product.value = loadedProduct;
      related.value = allProducts.filter((p) => loadedProduct.relatedIds.includes(p.id));
      category.value = categories.find((c) => c.id === loadedProduct.category) ?? null;
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Unknown error';
    } finally {
      loading.value = false;
    }
  }

  return { product, related, category, loading, error, load };
}
