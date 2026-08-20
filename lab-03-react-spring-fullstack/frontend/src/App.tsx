import { useEffect, useState } from 'react';
import { getMerchants, getPayment, updatePaymentStatus } from './api/payments';
import { usePayments } from './hooks/usePayments';
import type { Merchant, Payment, PaymentStatus } from './types/domain';

const statuses: Array<PaymentStatus | 'ALL'> = ['ALL','PENDING','CAPTURED','FAILED','REFUNDED'];
export default function App() {
 const [merchants,setMerchants]=useState<Merchant[]>([]); const [merchantId,setMerchantId]=useState<string|null>(null); const [status,setStatus]=useState<PaymentStatus|'ALL'>('ALL'); const [page,setPage]=useState(1); const [selected,setSelected]=useState<Payment|null>(null); const [message,setMessage]=useState('');
 const {data,error,loading}=usePayments(merchantId,status,page);
 useEffect(()=>{getMerchants().then(items=>{setMerchants(items); if(items[0])setMerchantId(items[0].id);}).catch(e=>setMessage(e.message));},[]);
 const chooseMerchant=(id:string)=>{setMerchantId(id);setPage(1);setSelected(null);};
 async function changeStatus(next: PaymentStatus){ if(!selected)return; try { await updatePaymentStatus(selected.id,next); selected.status=next; setSelected(selected); } catch(e) { setMessage(e instanceof Error?e.message:'Update failed'); } }
 return <main><header><p className="eyebrow">OPERATIONS / PAYMENTS</p><h1>Merchant Payments</h1><p className="lede">Trace every payment from merchant account to settlement state.</p></header>
  <section className="workspace"><aside><h2>Merchants</h2>{merchants.map(m=><button className={m.id===merchantId?'merchant active':'merchant'} key={m.id} onClick={()=>chooseMerchant(m.id)}><strong>{m.name}</strong><span>{m.id} · {m.active?'Active':'Inactive'}</span></button>)}</aside>
  <section className="payments"><div className="toolbar"><div><p className="eyebrow">PAYMENT LEDGER</p><h2>{merchantId ?? 'Choose a merchant'}</h2></div><label>Status <select aria-label="Payment status" value={status} onChange={e=>{setStatus(e.target.value as PaymentStatus|'ALL');setPage(1)}}>{statuses.map(s=><option key={s}>{s}</option>)}</select></label></div>
   {loading&&<p>Loading payments...</p>}{error&&<p role="alert">{error}</p>}{data&&<><div className="payment-list">{data.content.map(p=><button className="payment" key={p.id} onClick={()=>getPayment(p.id).then(setSelected).catch(e=>setMessage(e.message))}><span className={`dot ${p.status.toLowerCase()}`}></span><span><strong>{p.description||'Untitled payment'}</strong><small>{p.id.slice(-8)} · {new Date(p.createdAt).toLocaleDateString()}</small></span><b>${p.amount.toFixed(2)}</b><em>{p.status}</em></button>)}</div><nav className="pager"><button disabled={page===1} onClick={()=>setPage(page-1)}>Previous</button><span>Page {page} of {data.totalPages}</span><button disabled={page>=data.totalPages} onClick={()=>setPage(page+1)}>Next</button></nav></>}
  </section></section>
  {selected&&<section className="detail"><div><p className="eyebrow">PAYMENT DETAIL</p><h2>{selected.description||'Untitled payment'}</h2><p>{selected.merchantName} · {selected.merchantId}</p></div><dl><dt>Amount</dt><dd>${selected.amount.toFixed(2)}</dd><dt>Status</dt><dd>{selected.status}</dd><dt>Created</dt><dd>{new Date(selected.createdAt).toLocaleString()}</dd><dt>Payment ID</dt><dd>{selected.id}</dd></dl>{selected.status==='PENDING'&&<div className="actions"><button onClick={()=>changeStatus('CAPTURED')}>Capture</button><button onClick={()=>changeStatus('FAILED')}>Mark failed</button></div>}</section>}
  {message&&<p role="status" className="toast">{message}</p>}</main>;
}
