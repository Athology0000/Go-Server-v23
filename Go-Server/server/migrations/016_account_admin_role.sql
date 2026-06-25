-- L2: bind the admin role to the ACCOUNT, not to the pile of issued tokens. handleAdminLogin derived
-- a session's role from MAX(role) over an account's currently-valid admin_tokens, so issuing a
-- lower-role token never demoted and a lingering valid super_admin token re-granted on every login.
-- accounts.admin_role is now the single source of truth for login. NULL = not an admin.
ALTER TABLE accounts ADD COLUMN IF NOT EXISTS admin_role TEXT
    CHECK (admin_role IS NULL OR admin_role IN ('super_admin','support','viewer'));

-- Backfill existing admins from their highest currently-valid token role, so effective login access
-- is unchanged on deploy. From here the role is managed explicitly (token issue / bootstrap) and a
-- stale token can no longer re-grant at login.
UPDATE accounts a
SET admin_role = sub.role
FROM (
    SELECT admin_username, role,
           ROW_NUMBER() OVER (
               PARTITION BY admin_username
               ORDER BY CASE role WHEN 'super_admin' THEN 3 WHEN 'support' THEN 2 ELSE 1 END DESC
           ) AS rn
    FROM admin_tokens
    WHERE revoked = false AND expires_at > now()
) sub
WHERE a.username = sub.admin_username AND sub.rn = 1;
