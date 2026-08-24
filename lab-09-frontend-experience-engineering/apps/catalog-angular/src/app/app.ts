import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CmsService } from './cms.service';
import type { Category, Product } from './models';

type ViewMode = 'grid' | 'list';
type SortOrder = 'default' | 'price-asc' | 'price-desc';

@Component({
  selector: 'app-root',
  imports: [CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit {
  private cms = inject(CmsService);

  products = signal<Product[]>([]);
  categories = signal<Category[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  view = signal<ViewMode>('grid');
  selectedCategory = signal<string>('all');
  sortOrder = signal<SortOrder>('default');
  searchQuery = signal<string>('');
  expandedIds = signal<Set<string>>(new Set());

  filteredProducts = computed(() => {
    let items = this.products();
    const category = this.selectedCategory();
    if (category !== 'all') {
      items = items.filter((p) => p.category === category);
    }
    const query = this.searchQuery().trim().toLowerCase();
    if (query) {
      items = items.filter((p) => p.name.toLowerCase().includes(query));
    }
    const sort = this.sortOrder();
    if (sort === 'price-asc') {
      items = [...items].sort((a, b) => a.price - b.price);
    } else if (sort === 'price-desc') {
      items = [...items].sort((a, b) => b.price - a.price);
    }
    return items;
  });

  ngOnInit(): void {
    this.cms.getCategories().subscribe({
      next: (categories) => this.categories.set(categories),
    });
    this.cms.getProducts().subscribe({
      next: (products) => {
        this.products.set(products);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not load the product catalog.');
        this.loading.set(false);
      },
    });
  }

  setView(mode: ViewMode): void {
    this.view.set(mode);
  }

  selectCategory(id: string): void {
    this.selectedCategory.set(id);
  }

  onSortChange(event: Event): void {
    this.sortOrder.set((event.target as HTMLSelectElement).value as SortOrder);
  }

  onSearchChange(event: Event): void {
    this.searchQuery.set((event.target as HTMLInputElement).value);
  }

  isExpanded(id: string): boolean {
    return this.expandedIds().has(id);
  }

  toggleExpand(id: string): void {
    const next = new Set(this.expandedIds());
    if (next.has(id)) {
      next.delete(id);
    } else {
      next.add(id);
    }
    this.expandedIds.set(next);
  }

  availabilityLabel(availability: Product['availability']): string {
    switch (availability) {
      case 'in_stock':
        return 'In stock';
      case 'low_stock':
        return 'Low stock';
      case 'out_of_stock':
        return 'Out of stock';
    }
  }
}
