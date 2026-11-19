const safeLabels = new Set([
  'aadhaar', 'aadhaar card', 'consent', 'allow', 'continue', 'next', 'submit', 'verify',
  'done', 'cancel', 'back', 'digilocker', 'verify aadhaar otp', 'security pin',
  'select documents', 'allow access', 'retry', 'try again', 'success', 'failed',
  'access denied', 'permission required', 'loading'
]);
const label = e => {
  const value = (e.innerText || e.getAttribute('aria-label') || e.value || '').replace(/\s+/g, ' ').trim().toLowerCase();
  return safeLabels.has(value) ? value : (value ? '[redacted]' : '');
};
const visible = e => {
  const r = e.getBoundingClientRect(), s = getComputedStyle(e);
  return r.width > 0 && r.height > 0 && s.visibility !== 'hidden' && s.display !== 'none';
};
const pageText = (document.body?.innerText || '').toLowerCase();
const textSignals = ['consent', 'allow', 'aadhaar', 'document', 'verify', 'otp', 'pin', 'error', 'failed', 'success', 'redirect', 'expired', 'invalid', 'retry', 'transaction', 'authorization', 'loading', 'not found', '404'].filter(term => pageText.includes(term));
return {
  readyState: document.readyState,
  textSignals,
  headings: [...document.querySelectorAll('h1,h2,h3,[role=heading]')].filter(visible).slice(0, 12).map(label),
  buttons: [...document.querySelectorAll('button,input[type=submit],[role=button]')].filter(visible).slice(0, 30).map(e => ({label: label(e), enabled: !e.disabled})),
  inputs: [...document.querySelectorAll('input,textarea,select')].filter(visible).slice(0, 30).map(e => ({type: e.type || e.tagName.toLowerCase(), enabled: !e.disabled, valuePresent: !!e.value, valueLength: (e.value || '').length, checked: !!e.checked})),
  checkboxes: [...document.querySelectorAll('input[type=checkbox],[role=checkbox]')].filter(visible).slice(0, 20).map(e => ({checked: e.checked === true || e.getAttribute('aria-checked') === 'true', enabled: !e.disabled})),
  visibleLinkCount: [...document.querySelectorAll('a')].filter(visible).length
};
