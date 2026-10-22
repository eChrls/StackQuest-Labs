#!/usr/bin/env node
// Fails when a tracked file pins a PHP runtime older than the patched releases for
// CVE-2026-109618 / CVE-2026-109619 (PHP2Shell), or any PHP branch without security support.
// Floating tags (php:8.5-cli) pass: they resolve to the newest patch at build time.
import { execFileSync } from 'node:child_process';
import { readFileSync } from 'node:fs';

const MIN_PATCH = { '8.2': 35, '8.3': 36, '8.4': 27, '8.5': 12 };
const files = execFileSync('git', ['ls-files', '-z'], { encoding: 'utf8', maxBuffer: 64 * 1024 * 1024 })
  .split('\0').filter((f) => /(^|\/)(Dockerfile[^/]*|[^/]*\.ya?ml|profiles\.json|composer\.json)$/.test(f))
  .filter((f) => !/(^|\/)(node_modules|vendor|upstream|research)\//.test(f));

const problems = [];
const check = (file, line, version) => {
  const [maj, min, pat] = version.split('.');
  const branch = `${maj}.${min}`;
  if (!(branch in MIN_PATCH)) problems.push(`${file}:${line} PHP ${version}: branch ${branch} has no security support`);
  else if (pat !== undefined && Number(pat) < MIN_PATCH[branch]) problems.push(`${file}:${line} PHP ${version}: below patched ${branch}.${MIN_PATCH[branch]}`);
};

for (const file of files) {
  let text;
  try { text = readFileSync(file, 'utf8'); } catch { continue; }
  text.split(/\r?\n/).forEach((content, i) => {
    for (const m of content.matchAll(/\bphp:(\d+\.\d+(?:\.\d+)?)/g)) check(file, i + 1, m[1]);
    for (const m of content.matchAll(/php-version:\s*['"]?(\d+\.\d+(?:\.\d+)?)/g)) check(file, i + 1, m[1]);
  });
}
if (problems.length) { console.error(problems.join('\n')); process.exit(1); }
console.log(`PHP version guard OK (${files.length} files checked, minimums: ${Object.entries(MIN_PATCH).map(([b, p]) => `${b}.${p}`).join(', ')})`);
