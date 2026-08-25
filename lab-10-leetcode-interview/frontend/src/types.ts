export type TransferStatus='PENDING'|'COMPLETED'|'FAILED';
export interface Transfer {id:number;userId:string;amount:number;status:TransferStatus;createdAt:string}
export interface NewTransfer {userId:string;amount:number}
