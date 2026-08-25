import type {NewTransfer,Transfer,TransferStatus} from './types';
async function checked<T>(response:Response):Promise<T>{if(!response.ok)throw new Error((await response.json()).error??`HTTP ${response.status}`);return response.json()}
export const listTransfers=()=>fetch('/api/transfers').then(checked<Transfer[]>);
export const createTransfer=(value:NewTransfer,key:string)=>fetch('/api/transfers',{method:'POST',headers:{'Content-Type':'application/json','Idempotency-Key':key},body:JSON.stringify(value)}).then(checked<Transfer>);
export const updateTransferStatus=(id:number,status:TransferStatus)=>fetch(`/api/transfers/${id}/status`,{method:'PATCH',headers:{'Content-Type':'application/json'},body:JSON.stringify({status})}).then(checked<Transfer>);
