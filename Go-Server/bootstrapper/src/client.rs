use reqwest::blocking::Client;

pub fn build(server_url: &str) -> Result<Client, String> {
    let server_url = server_url.trim();

    // Enforce HTTPS for production (only allow HTTP for localhost development)
    if server_url.starts_with("http://")
        && !server_url.starts_with("http://localhost")
        && !server_url.starts_with("http://127.0.0.1")
    {
        return Err(
            "HTTPS is required for security. HTTP is only allowed for localhost development."
                .to_string(),
        );
    }

    Client::builder()
        .timeout(std::time::Duration::from_secs(30))
        .build()
        .map_err(|e| format!("Failed to build HTTP client: {e}"))
}

pub fn api_url(server_url: &str, path: &str) -> String {
    let base = server_url.trim().trim_end_matches('/');
    let path = path.trim_start_matches('/');

    if base.is_empty() {
        format!("/{path}")
    } else {
        format!("{base}/{path}")
    }
}

#[cfg(test)]
mod tests {
    use super::api_url;

    #[test]
    fn api_url_joins_with_one_slash() {
        assert_eq!(
            api_url("https://example.com/", "/enroll/handshake"),
            "https://example.com/enroll/handshake"
        );
        assert_eq!(
            api_url("https://example.com", "auth/start"),
            "https://example.com/auth/start"
        );
    }
}
