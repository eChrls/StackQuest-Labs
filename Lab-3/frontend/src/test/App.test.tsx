import { describe, it, expect, vi, beforeEach } from 'vitest';
import { cleanup, render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import App from '../App';
const merchant={id:'M1',name:'Northstar Market',active:true};
const payment=(id='00000000-0000-0000-0000-000000000001',status='PENDING')=>({id,merchantId:'M1',merchantName:'Northstar Market',amount:100,status,createdAt:'2026-01-01T10:00:00Z',description:'Order'});
function mockFetch(jsonUpdate=false){return vi.spyOn(globalThis,'fetch').mockImplementation(async (input,init)=>{const url=String(input); if(url.endsWith('/api/merchants'))return new Response(JSON.stringify([merchant])); if(url.includes('/payments?')){const page=new URL(url).searchParams.get('page'); return new Response(JSON.stringify({content:[payment(page==='1'?'p2':'p1')],page:Number(page),size:5,totalElements:6,totalPages:2}));} if(url.includes('/api/payments/')&&init?.method==='PATCH')return jsonUpdate?new Response(JSON.stringify({ok:true}),{status:200}):new Response(null,{status:204}); return new Response(JSON.stringify(payment()),{status:200});});}
beforeEach(()=>{cleanup();vi.restoreAllMocks();});
describe('dashboard behavior',()=>{
 it('loads merchants',async()=>{mockFetch();render(<App/>);expect(await screen.findByText('Northstar Market')).toBeInTheDocument();});
 it('shows payment rows',async()=>{mockFetch();render(<App/>);expect(await screen.findByText('Order')).toBeInTheDocument();});
 it('shows amount',async()=>{mockFetch();render(<App/>);expect(await screen.findByText('$100.00')).toBeInTheDocument();});
 it('shows status filter',()=>{mockFetch();render(<App/>);expect(screen.getByLabelText('Payment status')).toBeInTheDocument();});
 it('requests selected status',async()=>{mockFetch();render(<App/>);await screen.findByText('Order');await userEvent.selectOptions(screen.getByLabelText('Payment status'),'FAILED');await waitFor(()=>expect(fetch).toHaveBeenCalledWith(expect.stringContaining('status=FAILED')));});
 it('requests the backend page for visual page two',async()=>{mockFetch();render(<App/>);await screen.findByText('Order');await userEvent.click(screen.getByText('Next'));await waitFor(()=>expect(String(vi.mocked(fetch).mock.calls.at(-1)?.[0])).toContain('page=1'));});
 it('disables previous on first page',async()=>{mockFetch();render(<App/>);await screen.findByText('Order');expect(screen.getByText('Previous')).toBeDisabled();});
 it('navigates forward',async()=>{mockFetch();render(<App/>);await screen.findByText('Order');await userEvent.click(screen.getByText('Next'));expect(await screen.findByText('Page 2 of 2')).toBeInTheDocument();});
 it('opens payment detail',async()=>{mockFetch();render(<App/>);await screen.findByText('Order');await userEvent.click(screen.getByText('Order'));expect(await screen.findByText('Payment ID')).toBeInTheDocument();});
 it('offers pending transitions',async()=>{mockFetch();render(<App/>);await screen.findByText('Order');await userEvent.click(screen.getByText('Order'));expect(await screen.findByText('Capture')).toBeInTheDocument();});
 it('updates status without an error',async()=>{mockFetch();render(<App/>);await screen.findByText('Order');await userEvent.click(screen.getByText('Order'));await userEvent.click(screen.getByText('Capture'));await waitFor(()=>expect(screen.queryByRole('status')).not.toBeInTheDocument());});
 it('renders updated status immediately',async()=>{mockFetch(true);render(<App/>);await screen.findByText('Order');await userEvent.click(screen.getByText('Order'));await userEvent.click(screen.getByText('Capture'));expect(await screen.findByText('CAPTURED',{selector:'dd'})).toBeInTheDocument();});
 it('shows merchant identity',async()=>{mockFetch();render(<App/>);expect(await screen.findByText('M1 · Active')).toBeInTheDocument();});
 it('shows loading state for an initial request',async()=>{vi.spyOn(globalThis,'fetch').mockImplementation(async input=>String(input).endsWith('/api/merchants')?new Response(JSON.stringify([merchant])):new Promise(()=>{}) as Promise<Response>);render(<App/>);expect(await screen.findByText('Loading payments...')).toBeInTheDocument();});
});
