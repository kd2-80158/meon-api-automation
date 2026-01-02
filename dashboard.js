let allResults = [];

/* ================= LOAD DASHBOARD ================= */
function loadDashboard() {
    fetch("dashboard-results.json")   // ✅ correct path
        .then(res => {
            if (!res.ok) {
                throw new Error(`HTTP ${res.status}`);
            }
            return res.json();
        })
        .then(data => {
            allResults = data || [];
            renderSummary(allResults);
            renderInsights(allResults);
            renderModules(allResults);
            renderTable(allResults);
        })
        .catch(err => {
            console.error("Failed to load dashboard-results.json", err);
            document.getElementById("summary").innerText =
                "Unable to load dashboard-results.json";
        });
}


/* ================= SUMMARY ================= */
function renderSummary(data) {
	let pass = 0, fail = 0, skipped = 0;

	data.forEach(tc => {
		if (tc.status === "PASS") pass++;
		else if (tc.status === "FAIL") fail++;
		else if (tc.status === "SKIPPED") skipped++;
	});

	document.getElementById("summary").innerHTML = `
        <span>✅ PASS: ${pass}</span>
        <span>❌ FAIL: ${fail}</span>
        <span>⏸ SKIPPED: ${skipped}</span>
        <span>📊 TOTAL: ${data.length}</span>
    `;
}

/* ================= INSIGHTS ================= */
function renderInsights(data) {
	let pass = 0, fail = 0, totalTime = 0;

	data.forEach(tc => {
		if (tc.status === "PASS") pass++;
		if (tc.status === "FAIL") fail++;
		totalTime += tc.executionTimeMs || 0;
	});

	document.getElementById("passCount").innerText = pass;
	document.getElementById("failCount").innerText = fail;
	document.getElementById("totalCount").innerText = data.length;
	document.getElementById("avgTime").innerText =
		data.length ? Math.round(totalTime / data.length) : 0;
}

/* ================= MODULE HEALTH ================= */
function renderModules(data) {
	const map = {};

	data.forEach(tc => {
		const module = tc.apiModule || "unknown";
		if (!map[module]) map[module] = { pass: 0, total: 0 };

		map[module].total++;
		if (tc.status === "PASS") map[module].pass++;
	});

	const container = document.getElementById("modules");
	container.innerHTML = "";

	Object.entries(map).forEach(([module, stats]) => {
		container.innerHTML += `
            <div class="module-card">
                <strong>${module.split(".").pop()}</strong><br/>
                ${stats.pass}/${stats.total} PASS
            </div>
        `;
	});
}

/* ================= TABLE ================= */
function renderTable(data) {
	const tbody = document.getElementById("tableBody");
	tbody.innerHTML = "";

	data.forEach(tc => {
		const status = tc.status || "UNKNOWN";
		const execTime = tc.executionTimeMs || 0;

		let perf = "FAST";
		if (execTime > 3000) perf = "SLOW";
		else if (execTime > 1500) perf = "MEDIUM";

		const tr = document.createElement("tr");
		tr.className = status;

		tr.innerHTML = `
            <td title="${tc.className || ""}">
                ${tc.testCaseId || tc.testName || "-"}
            </td>

            <td>
                <span class="badge ${status}">${status}</span>
            </td>

            <td>${tc.apiModule?.split(".").pop() || "-"}</td>

            <td>${tc.httpStatus && tc.httpStatus !== 0 ? tc.httpStatus : "API"}</td>

            <td>${execTime} ms</td>

            <td><span class="perf ${perf}">${perf}</span></td>

			<td>
			    ${tc.error
				? `<details>
			              <summary>View Error</summary>
			              <pre class="error-box">${escapeHtml(tc.error)}</pre>
			           </details>`
				: "—"}
			</td>

            <td>${tc.timestamp ? new Date(tc.timestamp).toLocaleString() : "-"}</td>
			<td>${tc.executionType || "-"}</td>
			<td>${tc.executionSource || "-"}</td>
			<td>${tc.environment || "-"}</td>

			<td title="${tc.className}">
			  ${tc.className ? tc.className.split(".").pop() : "-"}
			</td>
        `;

		tbody.appendChild(tr);
	});
}

/* ================= FILTER ================= */
function filterStatus(status) {
	if (status === "ALL") {
		renderTable(allResults);
	} else {
		renderTable(allResults.filter(tc => tc.status === status));
	}
}
function escapeHtml(text) {
    if (!text) return "";
    return text
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;");
}

/* ================= INIT ================= */
window.onload = loadDashboard;
