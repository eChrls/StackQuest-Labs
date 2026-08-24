import { ref } from 'vue';
import type { HomeContent } from '../types';

export function useHome() {
  const data = ref<HomeContent | null>(null);
  const loading = ref(true);
  const error = ref<string | null>(null);

  async function load() {
    loading.value = true;
    error.value = null;
    try {
      const res = await fetch('/api/home');
      if (!res.ok) throw new Error(`CMS responded with ${res.status}`);
      data.value = (await res.json()) as HomeContent;
    } catch (err) {
      error.value = err instanceof Error ? err.message : 'Unknown error';
    } finally {
      loading.value = false;
    }
  }

  return { data, loading, error, load };
}
