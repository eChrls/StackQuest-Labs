export interface NavLink {
  label: string;
  href: string;
}

export interface Benefit {
  id: string;
  title: string;
  body: string;
}

export interface FooterColumn {
  title: string;
  links: NavLink[];
}

export interface HomeContent {
  site: {
    name: string;
    tagline: string;
    description: string;
    ogImage: string;
  };
  nav: NavLink[];
  hero: {
    eyebrow: string;
    headline: string;
    subheadline: string;
    ctaLabel: string;
    ctaHref: string;
    image: string;
  };
  benefits: Benefit[];
  featuredProductIds: string[];
  testimonial: {
    quote: string;
    author: string;
  };
  footer: {
    columns: FooterColumn[];
    note: string;
  };
}

export interface Product {
  id: string;
  name: string;
  category: string;
  price: number;
  currency: string;
  availability: 'in_stock' | 'low_stock' | 'out_of_stock';
  shortDescription: string;
  images: string[];
}
