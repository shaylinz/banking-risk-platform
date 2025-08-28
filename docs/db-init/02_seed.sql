INSERT INTO users (email, password_hash, full_name)
VALUES ('demo@bank.io', 'demo-hash', 'Demo User')
ON CONFLICT (email) DO NOTHING;

INSERT INTO accounts (user_id, account_type, balance)
SELECT id, 'CHECKING', 1250.00 FROM users WHERE email='demo@bank.io'
ON CONFLICT DO NOTHING;
