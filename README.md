# CodeFix AI

> **"Find. Understand. Fix. Verify."**  
> *Built for the IBM Bob 2.0 Hackathon*

---

## 1. Problem Statement

Developers spend over 35–45% of their working hours manually debugging software:
- Digging through log traces and minified exceptions
- Hypothesizing root causes across unfamiliar files
- Writing trial-and-error fixes that risk regressions
- Manually triggering test suites or writing missing test coverage

This fragmented workflow creates high cognitive load, context switching, and repetitive manual rework.

---

## 2. The Solution: CodeFix AI

**CodeFix AI** is an AI-powered developer productivity platform purpose-built for the core debugging lifecycle:
**Debugging + Code Analysis + Testing**.

### End-to-End Workflow:
```
Upload / Select Project
        ↓
   Analyze Code
        ↓
   Detect Issues
        ↓
   Explain Issues (What & Why)
        ↓
   Generate AI Fix (Gemini 3.5 Flash)
        ↓
 Compare Before / After Diff
        ↓
     Apply Fix
        ↓
  Run Automated Tests
        ↓
     Verify Fix
        ↓
  Generate Report
```

---

## 3. Technology Stack

- **Framework**: Modern Android with Jetpack Compose & Material 3
- **Language**: Kotlin 2.2+ (Coroutines, StateFlow, MVVM)
- **AI Engine**: Google AI Studio / Gemini API (`gemini-3.5-flash`)
- **Networking**: OkHttp 4.10 with resilient timeouts (60s)
- **Architecture**: Clean Architecture / MVVM with responsive layout (Mobile + Tablet NavigationRail)

---

## 4. Key Features

1. **AI Code Analysis**:
   - Safely parses project files, lines of code, and structure without arbitrary code execution.
   - Realistic multi-stage progress telemetry (Scanning → Understanding → Detecting → Explaining).

2. **Multi-Severity Bug Detection**:
   - Detects **Critical**, **High**, **Medium**, and **Low** issues across Categories:
     - Bug (Null/undefined dereferences, assignment in conditionals, division by zero)
     - Security (Hardcoded secrets, disabled token expiration)
     - Code Quality (EventEmitter memory leaks, unclosed listeners)
     - Performance & Maintainability

3. **In-Depth Issue Explanations**:
   - **What is wrong?**: Human-friendly explanation.
   - **Why does it happen?**: Technical root-cause breakdown.
   - **Current Code**: Monospaced syntax viewer with line numbers.
   - **AI Recommendation**: Prescriptive guide to safely remedy the defect.

4. **Gemini AI Fix Generation & Diff Viewer**:
   - Generates contextual fixes that preserve existing logic.
   - Side-by-side / stacked **Before vs After** code diff viewer with red deletion and green addition highlights.
   - Summarizes exact changes made and proposes unit tests.

5. **Fix Application & Regression Testing**:
   - Applying a fix updates the live project state.
   - Runs automated test suites (`test_valid_login`, `test_invalid_token`, `test_null_user_handling`, `test_discount_tier_gold`, `test_average_order_zero_safe`, `test_token_expiry_enforcement`).
   - Clearly flags:
     - `FAILED` before fix application (with real assertion failure traces)
     - `PASSED` after fix application
     - **FIX VERIFIED ✓** badge confirming zero regressions.

6. **Executive Productivity Report**:
   - Quantified time savings (e.g. 120m manual vs 35m CodeFix AI workflow → 85m saved).
   - Marked as **"Demo workflow estimate"**.
   - Visual Before vs After comparison diagram.

---

## 5. Built-in Demo Mode

The application is completely self-contained and **functions immediately out of the box** even without an external API key:
- Click **"Try Sample Project"** on the landing page.
- Experience the complete 2-minute demo workflow end-to-end.
- If Gemini API is configured in the Secrets panel or Settings, live calls to `gemini-3.5-flash` are performed seamlessly.

---

## 6. Security Protections

- **Zero Hardcoded Secrets**: Secrets are injected via `BuildConfig` and `.env`.
- **Safe Sandboxing**: Read-only source code inspection; never executes arbitrary shell commands or untrusted scripts.
- **Strict Output Validation**: AI JSON responses are validated before rendering.

---

## 7. IBM Bob 2.0 Hackathon Positioning

Positioned as an intelligent productivity accelerator for developers, QA engineers, open-source contributors, and development teams tackling code refactoring and automated bug remediation.
