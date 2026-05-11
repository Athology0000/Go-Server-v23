use reqwest::blocking::Client;

pub fn build(server_url: &str) -> Result<Client, String> {
    // Enforce HTTPS for production (only allow HTTP for localhost development)
    if server_url.starts_with("http://") 
        && !server_url.starts_with("http://localhost")
        && !server_url.starts_with("http://127.0.0.1") {
        return Err("HTTPS is required for security. HTTP is only allowed for localhost development.".to_string());
    }

    Client::builder()
        .timeout(std::time::Duration::from_secs(30))
        .build()
        .map_err(|e| format!("Failed to build HTTP client: {e}"))
}
