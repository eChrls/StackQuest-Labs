import '@testing-library/jest-dom/vitest';import{it,expect}from'vitest';import{render,screen}from'@testing-library/react';import{TransferHistory}from'./TransferHistory';
it('renders empty history',()=>{render(<TransferHistory items={[]} onTransition={async()=>{}}/>);expect(screen.getByText('No transfers yet.')).toBeInTheDocument()});
