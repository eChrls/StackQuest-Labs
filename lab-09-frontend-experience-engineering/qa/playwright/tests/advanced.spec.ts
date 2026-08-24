import { test, expect } from '@playwright/test';
import { getJsonLdBlocks, findByType } from '../helpers/jsonld';

test.describe('Advanced — Product detail (Vue)', () => {
  test('the gallery lets the user browse every product image', async ({ page }) => {
    await page.goto('/');
    const mainImage = page.locator('.gallery-main');
    const initialSrc = await mainImage.getAttribute('src');

    const thumbs = page.locator('.gallery-thumbs img');
    await expect(thumbs).toHaveCount(4);
    await thumbs.nth(1).click();

    await expect(mainImage).not.toHaveAttribute('src', initialSrc ?? '');
  });

  test('specifications are presented as an accessible disclosure', async ({ page }) => {
    await page.goto('/');
    const toggle = page.getByRole('button', { name: /specifications/i });
    await expect(toggle).toBeVisible();
    await expect(toggle).toHaveAttribute('aria-expanded', 'false');

    await toggle.click();
    await expect(toggle).toHaveAttribute('aria-expanded', 'true');
  });

  test('variant selection is keyboard-operable with correct roles and updates the price', async ({ page }) => {
    await page.goto('/');
    const priceText = await page.locator('.price').innerText();
    const basePrice = Number.parseFloat(priceText);

    const options = page.getByRole('radio');
    await expect(options).toHaveCount(3);

    await options.nth(2).focus();
    await expect(options.nth(2)).toBeFocused();
    await page.keyboard.press('Enter');
    await expect(options.nth(2)).toHaveAttribute('aria-checked', 'true');

    const updatedPriceText = await page.locator('.price').innerText();
    const updatedPrice = Number.parseFloat(updatedPriceText);
    expect(updatedPrice).not.toBe(basePrice);
  });

  test('ships Product, Offer and BreadcrumbList JSON-LD with real values', async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('.gallery-main')).toBeVisible();
    const blocks = await getJsonLdBlocks(page);

    const productBlock = findByType(blocks, 'Product');
    expect(productBlock, 'expected a Product JSON-LD block').toBeTruthy();
    expect(productBlock?.['name']).toBeTruthy();
    expect(productBlock?.['image']).toBeTruthy();

    const offer = productBlock?.['offers'] as Record<string, unknown> | undefined;
    expect(offer?.['@type']).toBe('Offer');
    expect(typeof offer?.['price']).toBe('number');
    expect(offer?.['priceCurrency']).toBeTruthy();
    expect(String(offer?.['availability'] ?? '')).toContain('schema.org');

    const breadcrumbs = findByType(blocks, 'BreadcrumbList');
    expect(breadcrumbs, 'expected a BreadcrumbList JSON-LD block').toBeTruthy();
    const items = breadcrumbs?.['itemListElement'];
    expect(Array.isArray(items) && items.length).toBe(3);
  });
});
