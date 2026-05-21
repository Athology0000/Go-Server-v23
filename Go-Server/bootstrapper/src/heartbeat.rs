use reqwest::blocking::Client;
use std::fs::File;
use std::io::{BufRead, BufReader, Seek, SeekFrom};
use std::path::Path;
use std::thread;
use std::time::{Duration, Instant};

pub fn start_heartbeat(client: &Client, server_url: &str, session_token: &str, game_dir: &str) {
    let client = client.clone();
    let server_url = server_url.to_string();
    let session_token = session_token.to_string();
    let game_dir = game_dir.to_string();

    thread::spawn(move || {
        heartbeat_loop(client, &server_url, &session_token, &game_dir);
    });
}

fn heartbeat_loop(client: Client, server_url: &str, session_token: &str, game_dir: &str) {
    let log_path = Path::new(game_dir).join("logs").join("latest.log");

    // Wait for log file to exist
    let mut last_pos = 0;
    let mut last_activity = Instant::now();

    loop {
        thread::sleep(Duration::from_secs(30)); // Check every 30 seconds

        if !log_path.exists() {
            continue;
        }

        let file = match File::open(&log_path) {
            Ok(f) => f,
            Err(_) => continue,
        };

        let mut reader = BufReader::new(file);
        if reader.seek(SeekFrom::Start(last_pos)).is_err() {
            last_pos = 0;
            continue;
        }

        let mut has_activity = false;
        let mut new_pos = last_pos;

        for line in reader.lines() {
            match line {
                Ok(line) => {
                    new_pos += line.len() as u64 + 1; // +1 for newline

                    // Check for module activity indicators
                    if line.contains("[Phantom]") ||
                       line.contains("is actively running") ||
                       line.contains("module loaded") ||
                       line.contains("injecting") ||
                       line.contains("hooking") {
                        has_activity = true;
                    }
                }
                Err(_) => break,
            }
        }

        last_pos = new_pos;

        if has_activity {
            last_activity = Instant::now();

            // Send heartbeat
            let url = format!("{}/auth/heartbeat", server_url);
            let body = serde_json::json!({
                "session_token": session_token
            });

            match client.post(&url).json(&body).send() {
                Ok(resp) => {
                    if resp.status().is_success() {
                        println!("Heartbeat sent successfully");
                    } else {
                        eprintln!("Heartbeat failed: {}", resp.status());
                    }
                }
                Err(e) => {
                    eprintln!("Heartbeat error: {}", e);
                }
            }
        } else if last_activity.elapsed() > Duration::from_secs(300) { // 5 minutes of inactivity
            // Stop heartbeats if no activity for 5 minutes
            break;
        }
    }

    println!("Heartbeat stopped due to inactivity");
}