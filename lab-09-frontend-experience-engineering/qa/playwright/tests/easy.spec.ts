import { test, expect } from '@playwright/test';
import { scanForA11yViolations } from '../helpers/axe';

test.describe('Easy — Landing (Vue)', () => {
  test('has semantic landmarks, a single h1, and no mobile overflow', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 812 });
    await page.goto('/');
    await expect(page.getByRole('heading', { level: 1 })).toHaveCount(1);
    await expect(page.getByRole('banner')).toBeVisible();
    await expect(page.getByRole('navigation')).toBeVisible();
    await expect(page.getByRole('main')).toBeVisible();
    await expect(page.getByRole('contentinfo')).toBeVisible();

    const { scrollWidth, clientWidth } = await page.evaluate(() => ({
      scrollWidth: document.documentElement.scrollWidth,
      clientWidth: document.documentElement.clientWidth,
    }));
    expect(scrollWidth, 'page should not require horizontal scrolling at 375px width').toBeLessThanOrEqual(
      clientWidth + 1,
    );
  });

  test('passes an automated WCAG 2.2 AA scan and shows a visible focus indicator', async ({ page }) => {
    await page.goto('/');
    const results = await scanForA11yViolations(page);
    const serious = results.violations.filter((v) => v.impact === 'serious' || v.impact === 'critical');
    expect(serious, JSON.stringify(serious, null, 2)).toEqual([]);

    const cta = page.getByRole('link', { name: /shop the collection/i });
    await cta.focus();
    const outline = await cta.evaluate((el) => {
      const style = getComputedStyle(el);
      return { outlineStyle: style.outlineStyle, outlineWidth: style.outlineWidth, boxShadow: style.boxShadow };
    });
    const hasVisibleFocus = outline.outlineStyle !== 'none' || outline.boxShadow !== 'none';
    expect(hasVisibleFocus, `expected a visible focus indicator, got ${JSON.stringify(outline)}`).toBe(true);
  });

  test('ships descriptive SEO metadata', async ({ page }) => {
    await page.goto('/');
    await expect(page.getByRole('heading', { level: 1 })).toBeVisible();
    await expect(page).toHaveTitle(/.{10,}/);

    const description = await page.locator('meta[name="description"]').getAttribute('content');
    expect(description?.length ?? 0).toBeGreaterThanOrEqual(50);

    const ogTitle = await page.locator('meta[property="og:title"]').getAttribute('content');
    const ogDescription = await page.locator('meta[property="og:description"]').getAttribute('content');
    const ogImage = await page.locator('meta[property="og:image"]').getAttribute('content');
    expect(ogTitle).toBeTruthy();
    expect(ogDescription).toBeTruthy();
    expect(ogImage).toBeTruthy();
  });
});
