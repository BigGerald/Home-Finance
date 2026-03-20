-- =========================================
-- EXTENSÃO PARA UUID
-- =========================================
CREATE
EXTENSION IF NOT EXISTS "pgcrypto";

-- =========================================
-- FUNÇÃO GLOBAL PARA updated_at
-- =========================================
CREATE
OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at
= CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

-- =========================================
-- TABELA: houses
-- =========================================
CREATE TABLE houses
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255)       NOT NULL,
    invite_code VARCHAR(50) UNIQUE NOT NULL,
    balance     DECIMAL(12, 2)   DEFAULT 0.00,
    admin_id    UUID               NOT NULL,
    created_at  TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_houses_admin_id ON houses (admin_id);

CREATE TRIGGER trg_update_houses_updated_at
    BEFORE UPDATE
    ON houses
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =========================================
-- TABELA: house_members
-- =========================================
CREATE TABLE house_members
(
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    house_id  UUID        NOT NULL REFERENCES houses (id) ON DELETE CASCADE,
    user_id   UUID        NOT NULL,
    role      VARCHAR(50) NOT NULL CHECK (role IN ('ADMIN', 'MEMBER')),
    status    VARCHAR(50) NOT NULL CHECK (status IN ('ACTIVE', 'LEFT', 'REMOVED')),
    joined_at TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP,
    left_at   TIMESTAMPTZ
);

-- 1 casa ativa por usuário
CREATE UNIQUE INDEX idx_unique_active_house_per_user
    ON house_members (user_id) WHERE status = 'ACTIVE';

CREATE INDEX idx_house_members_user_id ON house_members (user_id);

-- =========================================
-- TABELA: categories
-- =========================================
CREATE TABLE categories
(
    id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE
);

-- =========================================
-- TABELA: expenses
-- =========================================
CREATE TABLE expenses
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    house_id       UUID           NOT NULL REFERENCES houses (id) ON DELETE CASCADE,
    creator_id     UUID           NOT NULL,
    responsible_id UUID,
    title          VARCHAR(255)   NOT NULL,
    description    TEXT,
    amount         DECIMAL(12, 2) NOT NULL CHECK (amount > 0),
    category_id    UUID REFERENCES categories (id),
    due_date       DATE           NOT NULL,
    status         VARCHAR(50)    NOT NULL CHECK (status IN ('PENDING', 'PAID')),
    created_at     TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_expenses_creator_id ON expenses (creator_id);
CREATE INDEX idx_expenses_responsible_id ON expenses (responsible_id);
CREATE INDEX idx_expenses_house_id ON expenses (house_id);

CREATE TRIGGER trg_update_expenses_updated_at
    BEFORE UPDATE
    ON expenses
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =========================================
-- TABELA: expense_splits
-- =========================================
CREATE TABLE expense_splits
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    expense_id UUID           NOT NULL REFERENCES expenses (id) ON DELETE CASCADE,
    user_id    UUID           NOT NULL,
    amount     DECIMAL(12, 2) NOT NULL CHECK (amount > 0),
    status     VARCHAR(50)    NOT NULL CHECK (status IN ('PENDING', 'PAID')),
    created_at TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_expense_splits_user_id ON expense_splits (user_id);
CREATE INDEX idx_expense_splits_expense_id ON expense_splits (expense_id);

CREATE TRIGGER trg_update_expense_splits_updated_at
    BEFORE UPDATE
    ON expense_splits
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =========================================
-- TABELA: house_balance_transactions
-- =========================================
CREATE TABLE house_balance_transactions
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    house_id    UUID           NOT NULL REFERENCES houses (id) ON DELETE CASCADE,
    user_id     UUID           NOT NULL,
    amount      DECIMAL(12, 2) NOT NULL,
    type        VARCHAR(50)    NOT NULL CHECK (
        type IN ('MANUAL_ADD', 'MANUAL_REMOVE', 'EXPENSE_PAYMENT')
        ),
    description VARCHAR(255),
    created_at  TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_house_balance_transactions_user_id
    ON house_balance_transactions (user_id);