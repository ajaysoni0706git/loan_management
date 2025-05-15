Bank Loan Management System - API Documentation
===============================================

🔐 User Authentication
----------------------

1. Register a New User
----------------------
API:    POST http://localhost:8080/bankLoan/register
Desc:   Register a new customer or admin
Body:
{
  "name": "Abc",
  "email": "abc@test.com",
  "password": "1234",
  "address": "Dummy Address",
  "phone": "1234567890",
  "role": "USER"
}

2. Login
--------
API:    POST http://localhost:8080/bankLoan/login
Desc:   Authenticate and receive JWT token
Body:
{
  "email": "abc@test.com",
  "password": "1234"
}
Response:
{
  "token": "JWT_TOKEN_HERE",
  "user": {
    "id": 1,
    "name": "Abc",
    "email": "abc@test.com"
  }
}

👤 User Operations
------------------

3.1 Get Current Logged-in User Info
----------------------------------
API:    GET http://localhost:8080/bankLoan/getUser
API:    GET http://localhost:8080/bankLoan/getUser/{id}
Header: Authorization: Bearer <JWT_TOKEN>

3.2 Update Current Logged-in User Info
----------------------------------
API:    Put http://localhost:8080/bankLoan/updateUser/{id}

Header: Authorization: Bearer <JWT_TOKEN>

Body:
{
  "name": "Abc",
  "password": "12345",
  "address": "Dummy Address123",
  "phone": "15975368425",
}

💸 Loan Application
--------------------

4. Apply for a Loan
-------------------
API:    POST http://localhost:8080/bankLoan/loanApply
Header: Authorization: Bearer <JWT_TOKEN>
Body:
{
  "loanAmount": 100000,
  "interestRate": 10.5,
  "durationInMonths": 12,
  "purpose": "Business Expansion"
}

5. View All Loans for Current Customer
--------------------------------------
API:    GET http://localhost:8080/bankLoan/loans/my-loans
Header: Authorization: Bearer <JWT_TOKEN>

🧾 Loan Approval (Admin Only)
-----------------------------

6. Approve or Reject a Loan
---------------------------
API:    PUT http://localhost:8080/bankLoan/loans/approve/{loanId}?approve=true|false
Header: Authorization: Bearer <ADMIN_JWT_TOKEN>

💰 Loan Repayment
------------------

7. Make a Repayment
-------------------
API:    POST http://localhost:8080/bankLoan/loans/repay
Header: Authorization: Bearer <JWT_TOKEN>
Body:
{
  "loanId": 3,
  "amountPaid": 5000
}

⏰ Overdue Loans
----------------

8. Manually Trigger Overdue Loan Check (Admin Only)
---------------------------------------------------
API:    POST http://localhost:8080/bankLoan/loans/update-overdues
Header: Authorization: Bearer <ADMIN_JWT_TOKEN>

🔐 Security Notes
------------------
- All routes except `/register` and `/login` require a valid JWT token.
- Role checks:
    USER: can apply for loans and repay
    ADMIN: can approve loans and check overdue loans

Entities Involved:
------------------
- User
- LoanApplication
- Repayment

