import { useEffect, useState } from 'react';
import { getPayments } from '../api/payments';
import type { PaymentPage, PaymentStatus } from '../types/domain';
export function usePayments(merchantId: string | null, status: PaymentStatus | 'ALL', page: number) {
 const [data,setData]=useState<PaymentPage | null>(null); const [error,setError]=useState(''); const [loading,setLoading]=useState(false);
 useEffect(()=>{ if(!merchantId)return; setLoading(true); getPayments(merchantId,status,page).then(setData).catch(e=>setError(e.message)).finally(()=>setLoading(false)); },[merchantId,status,page]);
 return {data,error,loading};
}
