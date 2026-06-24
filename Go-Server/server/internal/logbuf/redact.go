package logbuf

import "regexp"

var (
	// secretJSONField blanks the value of known secret JSON fields, so a logged
	// request/response body can never leave a plaintext token (or proof/password)
	// in the buffer. Longer field names are listed first so the alternation does
	// not partially match a prefix.
	secretJSONField = regexp.MustCompile(`(?i)"(session_token|token|proof|password)"(\s*:\s*)"[^"]*"`)
	// bearerToken blanks an "Authorization: Bearer <token>" value.
	bearerToken = regexp.MustCompile(`(?i)(bearer\s+)[A-Za-z0-9._\-+/=]+`)
)

// redactSecrets removes plaintext secrets (bearer/session tokens, proofs,
// passwords) from a log line before it is retained in the in-memory buffer that
// GET /admin/server-logs serves. Non-secret diagnostics — account/device/session
// ids, token *hashes*, IPs, statuses — are left untouched so the logs stay
// useful. This is a defense-in-depth backstop: callers should still avoid logging
// raw bodies on auth endpoints, but anything that slips through is scrubbed here
// before it can be read back over HTTP.
func redactSecrets(line string) string {
	line = secretJSONField.ReplaceAllString(line, `"${1}"${2}"[REDACTED]"`)
	line = bearerToken.ReplaceAllString(line, `${1}[REDACTED]`)
	return line
}
