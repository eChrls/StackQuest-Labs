import type { Merchant, PaymentPage, PaymentStatus } from '../types/domain';
const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:18083';
export async function getMerchants(): Promise<Merchant[]> { const response = await fetch(`${baseUrl}/api/merchants`); if (!response.ok) throw new Error('Unable to load merchants'); return response.json(); }
export async function getPayments(merchantId: string, status: PaymentStatus | 'ALL', visualPage: number): Promise<PaymentPage> {
  const params = new URLSearchParams({ page: String(visualPage), size: '5' });
  if (status !== 'ALL') params.set('status', status);
  const response = await fetch(`${baseUrl}/api/merchants/${merchantId}/payments?${params}`); if (!response.ok) throw new Error('Unable to load payments'); return response.json();
}
export async function getPayment(id: string) { const response = await fetch(`${baseUrl}/api/payments/${id}`); if (!response.ok) throw new Error('Unable to load payment'); return response.json(); }
export async function updatePaymentStatus(id: string, status: PaymentStatus) { const response = await fetch(`${baseUrl}/api/payments/${id}/status`, { method: 'PATCH', headers: {'Content-Type':'application/json'}, body: JSON.stringify({status}) }); if (!response.ok) throw new Error('Unable to update payment'); return response.json(); }
