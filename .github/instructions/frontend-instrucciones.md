# FRONTEND DEVELOPMENT INSTRUCTIONS

Frontend must use simple hexagonal structure.

Structure must be:

src/

domain/

application
 ├ hooks
 └ store

infrastructure
 └ api

ui
 ├ pages
 └ components

main.jsx
index.css

Do not add folders without permission.

---

## LAYER RULES

domain

pure logic

no react
no api
no hooks

application

hooks
store

can use domain

infrastructure/api

http calls

only here fetch or axios

ui

components
pages

ui uses hooks or store

ui must not call api directly

no business logic in ui

---

## ORDER

1 domain
2 store/hooks
3 api
4 components
5 pages

Do not skip.

---

## TDD

Use TDD.

RED
GREEN
REFACTOR

Use

Vitest
Testing Library

Tests required.

---

## SOLID

Always apply.

Small components
No duplicated logic
Use hooks
Separate responsibilities

---

## PRD

Always validate.

If missing info → ask.

Do not assume.

---

## AMBIGUITY

Stop and ask.

Technical decisions allowed.

Business logic not.

---

## IMPROVEMENTS

You may propose.

Ask before applying.

---

## STRICT MODE

Warn if:

ui calls api
logic in ui
missing test
wrong layer
violates SOLID
not in PRD

Always guide next step.