import { test, expect } from '@playwright/test';
import { getJsonLdBlocks, findByType } from '../helpers/jsonld';

test.describe('Intermediate — Catalog (Angular)', () => {
  test('remembers the grid/list view preference across a reload', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: 'List' }).click();
    await expect(page.locator('.product-list')).toHaveCSS('display', 'block');

    await page.reload();
    await expect(page.locator('.product-list')).toHaveCSS('display', 'block');
  });

  test('category filter and price sort change the rendered results', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('button', { name: 'Lighting', exact: true }).click();
    await expect(page.locator('.card')).toHaveCount(3);

    await page.getByRole('button', { name: 'All', exact: true }).click();
    await page.locator('select').selectOption('price-desc');

    const prices = await page.locator('.card .price').allTextContents();
    const parsed = prices.map((text) => Number.parseFloat(text));
    expect(parsed[0]).toBeGreaterThanOrEqual(parsed[parsed.length - 1]);
  });

  test('a product card is keyboard-operable and exposes aria-expanded', async ({ page }) => {
    await page.goto('/');
    const trigger = page.locator('.card').first().locator('.expand-trigger');
    await expect(trigger).toHaveAttribute('aria-expanded', 'false');

    await trigger.focus();
    await expect(trigger).toBeFocused();
    await page.keyboard.press('Enter');
    await expect(trigger).toHaveAttribute('aria-expanded', 'true');
  });

  test('an empty search result shows an accessible empty state', async ({ page }) => {
    await page.goto('/');
    await page.getByPlaceholder('Search products').fill('zzz-no-such-product-zzz');
    await expect(page.getByText(/no products found/i)).toBeVisible();
  });

  test('ships canonical/Open Graph metadata and a valid ItemList schema', async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('.card').first()).toBeVisible();

    const canonicalLink = page.locator('link[rel="canonical"]');
    expect(await canonicalLink.count(), 'expected a canonical <link>').toBeGreaterThan(0);
    expect(await canonicalLink.first().getAttribute('href')).toBeTruthy();

    const ogTitleTag = page.locator('meta[property="og:title"]');
    expect(await ogTitleTag.count(), 'expected an og:title meta tag').toBeGreaterThan(0);
    expect(await ogTitleTag.first().getAttribute('content')).toBeTruthy();

    const blocks = await getJsonLdBlocks(page);
    const itemList = findByType(blocks, 'ItemList');
    expect(itemList, 'expected a Schema.org ItemList JSON-LD block').toBeTruthy();
    const items = itemList?.['itemListElement'];
    expect(Array.isArray(items) && items.length).toBeGreaterThan(0);
  });
});
