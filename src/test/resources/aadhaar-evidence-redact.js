const safeLabels = new Set([
  'aadhaar', 'aadhaar card', 'consent', 'allow', 'continue', 'next', 'submit', 'verify',
  'done', 'cancel', 'back', 'digilocker', 'verify aadhaar otp', 'security pin',
  'select documents', 'allow access', 'retry', 'try again', 'success', 'failed',
  'access denied', 'permission required', 'loading'
]);
const safe = e => safeLabels.has((e.textContent || '').replace(/\s+/g, ' ').trim().toLowerCase());
const walker = document.createTreeWalker(document.body, NodeFilter.SHOW_TEXT);
const nodes = [];
while (walker.nextNode()) nodes.push(walker.currentNode);
for (const node of nodes) {
  const parent = node.parentElement;
  const control = parent && parent.closest('button,[role=button],[role=checkbox],h1,h2,h3,h4');
  if (!control || !safe(control)) node.nodeValue = '[redacted]';
}
document.querySelectorAll('input,textarea').forEach(e => { e.value = '[redacted]'; e.setAttribute('value', '[redacted]'); });
document.querySelectorAll('img,canvas,video,svg').forEach(e => { e.style.visibility = 'hidden'; });
return true;
