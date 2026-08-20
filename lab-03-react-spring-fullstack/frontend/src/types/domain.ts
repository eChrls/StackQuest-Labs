export type PaymentStatus = 'PENDING' | 'CAPTURED' | 'FAILED' | 'REFUNDED';
export type Merchant = { id: string; name: string; active: boolean };
export type Payment = { id: string; merchantId: string; merchantName: string; amount: number; status: PaymentStatus; createdAt: string; description: string | null };
export type PaymentPage = { content: Payment[]; page: number; size: number; totalElements: number; totalPages: number };
