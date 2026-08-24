import type { Page } from '@playwright/test';
import AxeBuilder from '@axe-core/playwright';

/**
 * Runs axe-core scoped to the WCAG 2.2 AA rule set. This covers only the
 * subset of WCAG that is mechanically testable — it is evidence toward the
 * WCAG 2.2 AA target, not a certification. See the Lab README.
 */
export function scanForA11yViolations(page: Page) {
  return new AxeBuilder({ page }).withTags(['wcag2a', 'wcag2aa', 'wcag21aa', 'wcag22aa']).analyze();
}
