export interface VariantOption {
  id: string;
  label: string;
  swatch: string | null;
  priceDelta: number;
}

export interface Spec {
  label: string;
  value: string;
}

export interface Product {
  id: string;
  name: string;
  category: string;
  price: number;
  currency: string;
  availability: 'in_stock' | 'low_stock' | 'out_of_stock';
  rating: number;
  reviewCount: number;
  shortDescription: string;
  description: string;
  images: string[];
  variants: { label: string; options: VariantOption[] } | null;
  specs: Spec[];
  relatedIds: string[];
}

export interface Category {
  id: string;
  name: string;
}
