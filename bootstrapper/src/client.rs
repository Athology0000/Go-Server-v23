use reqwest::blocking::Client;
use reqwest::Certificate;

// Embed the server's DER certificate at compile time.
// Export with: openssl x509 -in server.crt -outform DER -out bootstrapper/certs/server.der
const SERVER_CERT_DER: &[u8] = include_bytes!("../certs/server.der");

pub fn build(_server_url: &str) -> Client {
    let cert = Certificate::from_der(SERVER_CERT_DER)
        .expect("Failed to parse pinned server certificate");

    Client::builder()
        .tls_built_in_root_certs(false)
        .add_root_certificate(cert)
        .timeout(std::time::Duration::from_secs(30))
        .build()
        .expect("Failed to build HTTP client")
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn cert_parses() {
        Certificate::from_der(SERVER_CERT_DER).expect("server.der must be valid DER");
    }
}
