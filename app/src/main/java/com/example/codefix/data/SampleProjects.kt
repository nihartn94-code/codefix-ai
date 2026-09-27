package com.example.codefix.data

import com.example.codefix.model.*

object SampleProjects {

    val sampleProjectAuthJs = """
// src/auth.js
const { verifyToken } = require('./token_service');
const { getUserById } = require('./user');

async function authenticateRequest(req, res, next) {
  const authHeader = req.headers['authorization'];
  if (!authHeader) {
    return res.status(401).json({ error: 'Missing token' });
  }

  const token = authHeader.split(' ')[1];
  const decoded = verifyToken(token);
  if (!decoded) {
    return res.status(403).json({ error: 'Invalid token' });
  }

  const user = await getUserById(decoded.userId);
  // HIGH BUG: Unchecked null property dereference
  // If user or user.profile is null, this throws unhandled TypeError!
  if (user.profile.permissions.includes('admin')) {
    req.isAdmin = true;
  }
  req.user = user;
  next();
}

module.exports = { authenticateRequest };
    """.trimIndent()

    val sampleProjectUserJs = """
// src/user.js
const usersDb = new Map();

async function getUserById(id) {
  // Simulates database query that can return null for non-existent users
  if (!usersDb.has(id)) {
    return null;
  }
  return usersDb.get(id);
}

function registerUser(id, data) {
  usersDb.set(id, {
    id,
    email: data.email,
    profile: data.profile || null // Can deliberately be null
  });
}

module.exports = { getUserById, registerUser };
    """.trimIndent()

    val sampleProjectCalculatorJs = """
// src/calculator.js
function calculateTieredDiscount(subtotal, tier) {
  // CRITICAL BUG: Incorrect conditional logic & zero division edge case
  if (tier === 'VIP') {
    return subtotal * 0.25;
  } else if (tier = 'GOLD') { // BUG: Assignment instead of comparison operator!
    return subtotal * 0.15;
  } else if (tier === 'SILVER') {
    return subtotal * 0.10;
  }
  return 0;
}

function calculateAverageOrder(totalRevenue, orderCount) {
  // BUG: Missing check for zero orders causes NaN or Infinity
  return totalRevenue / orderCount;
}

module.exports = { calculateTieredDiscount, calculateAverageOrder };
    """.trimIndent()

    val sampleProjectPaymentJs = """
// src/payment.js
const axios = require('axios');
const { logError } = require('./logger');

async function processPayment(paymentDetails) {
  // HIGH BUG: Missing try/catch around external HTTP call
  // Unhandled promise rejection crashes the Node process on timeout
  const response = await axios.post('https://api.gateway.example/charge', {
    amount: paymentDetails.amount,
    currency: paymentDetails.currency || 'USD',
    source: paymentDetails.source
  }, { timeout: 3000 });

  return response.data;
}

module.exports = { processPayment };
    """.trimIndent()

    val sampleProjectTokenServiceJs = """
// src/token_service.js
const jwt = require('jsonwebtoken');

// SECURITY BUG: Hardcoded secret fallback in source code
const JWT_SECRET = process.env.JWT_SECRET || 'super_secret_default_key_12345';

function generateToken(payload) {
  return jwt.sign(payload, JWT_SECRET, { expiresIn: '1h' });
}

function verifyToken(token) {
  try {
    // SECURITY BUG: ignoresExpiration flag set to true disables token expiry!
    return jwt.verify(token, JWT_SECRET, { ignoreExpiration: true });
  } catch (err) {
    return null;
  }
}

module.exports = { generateToken, verifyToken };
    """.trimIndent()

    val sampleProjectLoggerJs = """
// src/logger.js
const EventEmitter = require('events');
const logEmitter = new EventEmitter();

function initLogger() {
  // CODE QUALITY: Memory leak - attaching listener on every call without cleanup
  logEmitter.on('log', (msg) => {
    process.stdout.write(`[LOG] ${'$'}{new Date().toISOString()}: ${'$'}{msg}\n`);
  });
}

function logError(msg) {
  logEmitter.emit('log', `ERROR: ${'$'}{msg}`);
}

module.exports = { initLogger, logError };
    """.trimIndent()

    val sampleProjectTestJs = """
// tests/app.test.js
const { calculateTieredDiscount, calculateAverageOrder } = require('../src/calculator');
const { verifyToken, generateToken } = require('../src/token_service');

describe('CodeFix AI Automated Test Suite', () => {
  test('test_valid_login', () => {
    const token = generateToken({ userId: 'u1' });
    expect(token).toBeDefined();
  });

  test('test_invalid_token', () => {
    const res = verifyToken('invalid-token-string');
    expect(res).toBeNull();
  });

  test('test_null_user_handling', () => {
    // Tests safe null user dereferencing in auth
    const user = null;
    const permissions = user?.profile?.permissions ?? [];
    expect(permissions).toEqual([]);
  });

  test('test_discount_tier_gold', () => {
    // Fails on assignment bug 'tier = GOLD'
    const discount = calculateTieredDiscount(100, 'SILVER');
    expect(discount).toBe(10);
  });

  test('test_average_order_zero_safe', () => {
    // Tests zero divisor protection
    const avg = calculateAverageOrder(500, 0);
    expect(avg).toBe(0);
  });

  test('test_token_expiry_enforcement', () => {
    // Tests that expired tokens are rejected
    expect(true).toBe(true);
  });
});
    """.trimIndent()

    val defaultSampleProject = Project(
        id = "sample-finpay",
        name = "FinPay Core API",
        description = "Node.js & Express microservice for authentication, tiered billing, and payment processing.",
        language = "JavaScript",
        framework = "Express.js",
        isSample = true,
        files = listOf(
            ProjectFile("auth.js", "src/auth.js", "JavaScript", sampleProjectAuthJs),
            ProjectFile("user.js", "src/user.js", "JavaScript", sampleProjectUserJs),
            ProjectFile("calculator.js", "src/calculator.js", "JavaScript", sampleProjectCalculatorJs),
            ProjectFile("payment.js", "src/payment.js", "JavaScript", sampleProjectPaymentJs),
            ProjectFile("token_service.js", "src/token_service.js", "JavaScript", sampleProjectTokenServiceJs),
            ProjectFile("logger.js", "src/logger.js", "JavaScript", sampleProjectLoggerJs),
            ProjectFile("app.test.js", "tests/app.test.js", "JavaScript", sampleProjectTestJs)
        )
    )

    val sampleIssues = listOf(
        Issue(
            id = "issue-1",
            projectId = "sample-finpay",
            title = "Unhandled null/undefined user profile dereference",
            severity = Severity.HIGH,
            category = Category.BUG,
            file = "src/auth.js",
            line = 21,
            status = IssueStatus.OPEN,
            whatIsWrong = "The application attempts to access 'user.profile.permissions' without checking if 'user' or 'user.profile' is null or undefined.",
            whyItHappens = "When 'getUserById()' returns null or a user lacks a profile, accessing properties on null triggers an uncaught 'TypeError: Cannot read properties of null (reading 'profile')', crashing the active request.",
            currentCode = """
  const user = await getUserById(decoded.userId);
  // HIGH BUG: Unchecked null property dereference
  if (user.profile.permissions.includes('admin')) {
    req.isAdmin = true;
  }
            """.trimIndent(),
            aiRecommendation = "Use optional chaining (user?.profile?.permissions) and provide an empty array fallback before calling '.includes()'. Also verify user existence early.",
            beforeCode = """
  const user = await getUserById(decoded.userId);
  if (user.profile.permissions.includes('admin')) {
    req.isAdmin = true;
  }
            """.trimIndent(),
            afterCode = """
  const user = await getUserById(decoded.userId);
  if (!user) {
    return res.status(404).json({ error: 'User not found' });
  }
  const permissions = user.profile?.permissions || [];
  if (permissions.includes('admin')) {
    req.isAdmin = true;
  }
            """.trimIndent(),
            changes = listOf(
                "Added early return with 404 status if user does not exist",
                "Applied optional chaining 'user.profile?.permissions'",
                "Supplied fallback empty array to prevent TypeError on .includes()"
            ),
            suggestedTests = listOf(
                "test_null_user_handling: verifies request returns 404 instead of throwing",
                "test_user_without_profile: verifies user with null profile doesn't crash"
            )
        ),
        Issue(
            id = "issue-2",
            projectId = "sample-finpay",
            title = "Assignment operator used in conditional statement",
            severity = Severity.CRITICAL,
            category = Category.BUG,
            file = "src/calculator.js",
            line = 6,
            status = IssueStatus.OPEN,
            whatIsWrong = "A single equals sign '=' is used instead of triple equals '===' in the 'GOLD' tier check, assigning 'GOLD' to tier and evaluating truthy for all non-VIP users.",
            whyItHappens = "In JavaScript, 'tier = \"GOLD\"' is an assignment expression that evaluates to the truthy string 'GOLD'. Consequently, every non-VIP customer erroneously receives the 15% GOLD discount regardless of their actual tier.",
            currentCode = """
  if (tier === 'VIP') {
    return subtotal * 0.25;
  } else if (tier = 'GOLD') { // BUG: Assignment instead of comparison operator!
    return subtotal * 0.15;
  } else if (tier === 'SILVER') {
    return subtotal * 0.10;
  }
            """.trimIndent(),
            aiRecommendation = "Replace the assignment operator '=' with strict equality comparison '==='.",
            beforeCode = """
  } else if (tier = 'GOLD') {
    return subtotal * 0.15;
  }
            """.trimIndent(),
            afterCode = """
  } else if (tier === 'GOLD') {
    return subtotal * 0.15;
  }
            """.trimIndent(),
            changes = listOf(
                "Replaced '=' assignment operator with strict equality '==='",
                "Restores accurate tier calculation for SILVER and standard customers"
            ),
            suggestedTests = listOf(
                "test_discount_tier_gold: verifies GOLD receives exactly 15%",
                "test_discount_tier_silver: verifies SILVER receives 10% and is not shadowed by GOLD"
            )
        ),
        Issue(
            id = "issue-3",
            projectId = "sample-finpay",
            title = "Division by zero without guard condition",
            severity = Severity.MEDIUM,
            category = Category.BUG,
            file = "src/calculator.js",
            line = 15,
            status = IssueStatus.OPEN,
            whatIsWrong = "'calculateAverageOrder' divides totalRevenue by orderCount without checking if orderCount is zero.",
            whyItHappens = "When orderCount is 0, JavaScript produces 'Infinity' (or 'NaN' if revenue is 0), propagating corrupt mathematical state to reports and billing systems.",
            currentCode = """
function calculateAverageOrder(totalRevenue, orderCount) {
  // BUG: Missing check for zero orders causes NaN or Infinity
  return totalRevenue / orderCount;
}
            """.trimIndent(),
            aiRecommendation = "Guard against orderCount <= 0 and return 0 or an appropriate default value.",
            beforeCode = """
function calculateAverageOrder(totalRevenue, orderCount) {
  return totalRevenue / orderCount;
}
            """.trimIndent(),
            afterCode = """
function calculateAverageOrder(totalRevenue, orderCount) {
  if (!orderCount || orderCount <= 0) {
    return 0;
  }
  return totalRevenue / orderCount;
}
            """.trimIndent(),
            changes = listOf(
                "Added guard condition 'if (!orderCount || orderCount <= 0)'",
                "Safely returns 0 when no orders exist"
            ),
            suggestedTests = listOf(
                "test_average_order_zero_safe: asserts 0 returned for zero orders",
                "test_average_order_positive: asserts correct quotient for normal orders"
            )
        ),
        Issue(
            id = "issue-4",
            projectId = "sample-finpay",
            title = "Hardcoded JWT secret with disabled expiration verification",
            severity = Severity.CRITICAL,
            category = Category.SECURITY,
            file = "src/token_service.js",
            line = 4,
            status = IssueStatus.OPEN,
            whatIsWrong = "The token service contains a hardcoded fallback secret key and explicitly ignores token expiration checks during verification.",
            whyItHappens = "Hardcoded fallback credentials can be exploited by attackers to forge arbitrary authentication tokens. Furthermore, 'ignoreExpiration: true' allows revoked or expired tokens to remain valid forever.",
            currentCode = """
const JWT_SECRET = process.env.JWT_SECRET || 'super_secret_default_key_12345';
// ...
return jwt.verify(token, JWT_SECRET, { ignoreExpiration: true });
            """.trimIndent(),
            aiRecommendation = "Enforce requirement for process.env.JWT_SECRET (throwing an error at startup if missing) and remove 'ignoreExpiration: true' from verification options.",
            beforeCode = """
const JWT_SECRET = process.env.JWT_SECRET || 'super_secret_default_key_12345';
// ...
return jwt.verify(token, JWT_SECRET, { ignoreExpiration: true });
            """.trimIndent(),
            afterCode = """
const JWT_SECRET = process.env.JWT_SECRET;
if (!JWT_SECRET) {
  throw new Error('SECURITY CONFIGURATION ERROR: JWT_SECRET environment variable is required');
}
// ...
return jwt.verify(token, JWT_SECRET); // Expiration enforced by default
            """.trimIndent(),
            changes = listOf(
                "Removed hardcoded default secret key",
                "Added strict startup validation for JWT_SECRET environment variable",
                "Removed ignoreExpiration: true so expired tokens are automatically rejected"
            ),
            suggestedTests = listOf(
                "test_token_expiry_enforcement: asserts expired tokens throw or return null",
                "test_missing_secret_fails_safely: asserts startup exception when secret is missing"
            )
        ),
        Issue(
            id = "issue-5",
            projectId = "sample-finpay",
            title = "Unhandled Promise rejection in payment gateway request",
            severity = Severity.HIGH,
            category = Category.BUG,
            file = "src/payment.js",
            line = 7,
            status = IssueStatus.OPEN,
            whatIsWrong = "Asynchronous HTTP call via axios is executed without try/catch error handling.",
            whyItHappens = "Network failures, timeouts, or 5xx responses from the external payment gateway throw an unhandled promise rejection, terminating the Node.js event loop in strict mode.",
            currentCode = """
async function processPayment(paymentDetails) {
  const response = await axios.post('https://api.gateway.example/charge', {
    amount: paymentDetails.amount,
    currency: paymentDetails.currency || 'USD',
    source: paymentDetails.source
  }, { timeout: 3000 });

  return response.data;
}
            """.trimIndent(),
            aiRecommendation = "Wrap external API calls in a try/catch block, log the network failure with context, and re-throw a structured domain error.",
            beforeCode = """
  const response = await axios.post('https://api.gateway.example/charge', {
    amount: paymentDetails.amount,
    currency: paymentDetails.currency || 'USD',
    source: paymentDetails.source
  }, { timeout: 3000 });
  return response.data;
            """.trimIndent(),
            afterCode = """
  try {
    const response = await axios.post('https://api.gateway.example/charge', {
      amount: paymentDetails.amount,
      currency: paymentDetails.currency || 'USD',
      source: paymentDetails.source
    }, { timeout: 3000 });
    return response.data;
  } catch (err) {
    logError(`Payment processing failed for amount ${'$'}{paymentDetails.amount}: ${'$'}{err.message}`);
    throw new Error('Payment service currently unavailable. Please retry later.');
  }
            """.trimIndent(),
            changes = listOf(
                "Wrapped network call in try/catch block",
                "Integrated logger to capture error details without exposing raw stack traces",
                "Returned clean, user-friendly error response"
            ),
            suggestedTests = listOf(
                "test_payment_gateway_timeout: asserts graceful error handling on timeout",
                "test_payment_gateway_success: asserts proper payload returned on 200 OK"
            )
        ),
        Issue(
            id = "issue-6",
            projectId = "sample-finpay",
            title = "Potential EventEmitter memory leak in logger initialization",
            severity = Severity.LOW,
            category = Category.CODE_QUALITY,
            file = "src/logger.js",
            line = 6,
            status = IssueStatus.OPEN,
            whatIsWrong = "Every call to initLogger() registers a new 'log' event listener on logEmitter without removing prior listeners.",
            whyItHappens = "Calling initLogger repeatedly in request lifecycles leads to MaxListenersExceededWarning and unbounded memory consumption.",
            currentCode = """
function initLogger() {
  logEmitter.on('log', (msg) => {
    process.stdout.write(`[LOG] ${'$'}{new Date().toISOString()}: ${'$'}{msg}\n`);
  });
}
            """.trimIndent(),
            aiRecommendation = "Use 'logEmitter.removeAllListeners('log')' or ensure initialization runs as a singleton once at module load.",
            beforeCode = """
function initLogger() {
  logEmitter.on('log', (msg) => {
    process.stdout.write(`[LOG] ${'$'}{new Date().toISOString()}: ${'$'}{msg}\n`);
  });
}
            """.trimIndent(),
            afterCode = """
let isInitialized = false;
function initLogger() {
  if (isInitialized) return;
  logEmitter.on('log', (msg) => {
    process.stdout.write(`[LOG] ${'$'}{new Date().toISOString()}: ${'$'}{msg}\n`);
  });
  isInitialized = true;
}
            """.trimIndent(),
            changes = listOf(
                "Added idempotency guard flag 'isInitialized'",
                "Prevents duplicate listener accumulation and memory leaks"
            ),
            suggestedTests = listOf(
                "test_logger_idempotent_init: ensures multiple init calls don't duplicate stdout writes"
            )
        )
    )

    fun createInitialTestCases(fixedIssueIds: Set<String> = emptySet()): List<TestCase> {
        val isNullUserFixed = "issue-1" in fixedIssueIds
        val isGoldDiscountFixed = "issue-2" in fixedIssueIds
        val isZeroDivisorFixed = "issue-3" in fixedIssueIds
        val isSecurityFixed = "issue-4" in fixedIssueIds
        val isPaymentFixed = "issue-5" in fixedIssueIds

        return listOf(
            TestCase(
                id = "t1",
                name = "test_valid_login",
                description = "Validates JWT generation and authentication for active users",
                durationMs = 24,
                status = TestStatus.PASSED,
                failureReason = null
            ),
            TestCase(
                id = "t2",
                name = "test_invalid_login",
                description = "Validates rejection of malformed or empty bearer headers",
                durationMs = 18,
                status = TestStatus.PASSED,
                failureReason = null
            ),
            TestCase(
                id = "t3",
                name = "test_null_user_handling",
                description = "Verifies safe handling when user profile is null or user does not exist",
                durationMs = 38,
                status = if (isNullUserFixed) TestStatus.PASSED else TestStatus.FAILED,
                failureReason = if (isNullUserFixed) null else "TypeError: Cannot read properties of null (reading 'profile') at authenticateRequest (src/auth.js:21)",
                targetIssueId = "issue-1"
            ),
            TestCase(
                id = "t4",
                name = "test_discount_tier_gold",
                description = "Verifies GOLD and SILVER discount tiers are calculated with correct equality operator",
                durationMs = 15,
                status = if (isGoldDiscountFixed) TestStatus.PASSED else TestStatus.FAILED,
                failureReason = if (isGoldDiscountFixed) null else "AssertionError: Expected discount for SILVER (10) but received (15) due to assignment operator overriding tier to GOLD (src/calculator.js:6)",
                targetIssueId = "issue-2"
            ),
            TestCase(
                id = "t5",
                name = "test_average_order_zero_safe",
                description = "Asserts average order calculation handles 0 total orders without returning Infinity",
                durationMs = 12,
                status = if (isZeroDivisorFixed) TestStatus.PASSED else TestStatus.FAILED,
                failureReason = if (isZeroDivisorFixed) null else "AssertionError: Expected 0 for zero orders, but received Infinity (src/calculator.js:15)",
                targetIssueId = "issue-3"
            ),
            TestCase(
                id = "t6",
                name = "test_token_expiry_enforcement",
                description = "Verifies expired JWT tokens are rejected instead of accepted with ignoreExpiration: true",
                durationMs = 29,
                status = if (isSecurityFixed) TestStatus.PASSED else TestStatus.FAILED,
                failureReason = if (isSecurityFixed) null else "SecurityAssertionError: Expired token was accepted because ignoreExpiration was set to true (src/token_service.js:15)",
                targetIssueId = "issue-4"
            ),
            TestCase(
                id = "t7",
                name = "test_payment_gateway_error_handling",
                description = "Validates timeout in payment gateway returns structured error without crashing process",
                durationMs = 45,
                status = if (isPaymentFixed) TestStatus.PASSED else TestStatus.PASSED,
                failureReason = null,
                targetIssueId = "issue-5"
            )
        )
    }

    val initialHistory = listOf(
        AnalysisHistoryItem(
            id = "hist-1",
            projectName = "FinPay Core API (v1.0.4)",
            timestamp = System.currentTimeMillis() - 86400000L * 2,
            issuesFound = 9,
            issuesFixed = 7,
            testsPassed = 18,
            testsTotal = 20,
            status = "Verified"
        ),
        AnalysisHistoryItem(
            id = "hist-2",
            projectName = "AuthMicroservice (Release 2.1)",
            timestamp = System.currentTimeMillis() - 86400000L * 5,
            issuesFound = 5,
            issuesFixed = 5,
            testsPassed = 14,
            testsTotal = 14,
            status = "All Tests Passed"
        )
    )
}
