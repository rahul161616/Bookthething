# Security Policy

## Supported Versions

We release patches for security vulnerabilities in the following versions:

| Version | Supported          |
| ------- | ------------------ |
| 0.0.x   | :white_check_mark: |

## Reporting a Vulnerability

We take the security of our software seriously. If you believe you have found a security vulnerability in Bookthething, please report it to us as described below.

### Where to Report

Please report security vulnerabilities by email to: [security@example.com] (replace with your actual security contact)

**Please do not report security vulnerabilities through public GitHub issues.**

### What to Include

Please include the following information in your report:

- Type of issue (e.g. buffer overflow, SQL injection, JWT manipulation, etc.)
- Full paths of source file(s) related to the manifestation of the issue
- The location of the affected source code (tag/branch/commit or direct URL)
- Any special configuration required to reproduce the issue
- Step-by-step instructions to reproduce the issue
- Proof-of-concept or exploit code (if possible)
- Impact of the issue, including how an attacker might exploit the issue

### Response Timeline

- We will acknowledge receipt of your vulnerability report within 3 business days
- We will provide a detailed response within 7 business days indicating our next steps
- We will notify you when the vulnerability has been fixed

### Security Measures

Our application implements the following security measures:

1. **JWT Authentication**: All API endpoints require valid JWT tokens
2. **CORS Protection**: Configured to allow only trusted origins
3. **Input Validation**: All user inputs are validated and sanitized
4. **Database Security**: Parameterized queries prevent SQL injection
5. **Secure Headers**: Security headers are implemented in the gateway

### Responsible Disclosure

We ask that you:

- Give us reasonable time to fix the issue before publishing it
- Don't access or modify data that isn't your own
- Don't disrupt our service or degrade the user experience
- Don't post, transmit, upload, link to, send, or store malicious content

### Recognition

We appreciate the security research community's efforts to responsibly disclose vulnerabilities and will acknowledge your contribution if you wish to be credited.

Thank you for helping keep Bookthething and our users safe!