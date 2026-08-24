import type { Page } from '@playwright/test';

/**
 * Reads every <script type="application/ld+json"> block on the page and
 * parses it. Tests assert on the parsed objects, not on tag presence alone.
 */
export async function getJsonLdBlocks(page: Page): Promise<unknown[]> {
  const raw = await page.locator('script[type="application/ld+json"]').allTextContents();
  return raw.map((text) => JSON.parse(text));
}

export function findByType(blocks: unknown[], type: string): Record<string, unknown> | undefined {
  return blocks.find(
    (block): block is Record<string, unknown> =>
      typeof block === 'object' && block !== null && (block as { '@type'?: unknown })['@type'] === type,
  );
}
