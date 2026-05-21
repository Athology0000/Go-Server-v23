#!/usr/bin/env python3
"""
Dev helper for signing a manifest payload with Ed25519.

Install dependency:
  python -m pip install cryptography

Generate keypair:
  python scripts/sign_manifest.py --generate

Sign a manifest:
  python scripts/sign_manifest.py --private-key ed25519_private.pem --manifest manifest.json
"""
import argparse, base64, json, sys
from pathlib import Path
from cryptography.hazmat.primitives import serialization
from cryptography.hazmat.primitives.asymmetric import ed25519


def generate():
    private_key = ed25519.Ed25519PrivateKey.generate()
    public_key = private_key.public_key()

    private_pem = private_key.private_bytes(
        encoding=serialization.Encoding.PEM,
        format=serialization.PrivateFormat.PKCS8,
        encryption_algorithm=serialization.NoEncryption(),
    )
    public_der = public_key.public_bytes(
        encoding=serialization.Encoding.DER,
        format=serialization.PublicFormat.SubjectPublicKeyInfo,
    )

    Path("ed25519_private.pem").write_bytes(private_pem)
    print("Wrote ed25519_private.pem")
    print("Set this as -Dcobalt.manifest.publicKey:")
    print(base64.b64encode(public_der).decode())


def sign(private_key_path: Path, manifest_path: Path):
    private_key = serialization.load_pem_private_key(private_key_path.read_bytes(), password=None)
    payload = manifest_path.read_bytes()
    signature = private_key.sign(payload)
    out = {
        "payloadBase64": base64.b64encode(payload).decode(),
        "signatureBase64": base64.b64encode(signature).decode(),
    }
    print(json.dumps(out, indent=2))


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--generate", action="store_true")
    parser.add_argument("--private-key")
    parser.add_argument("--manifest")
    args = parser.parse_args()

    if args.generate:
        generate()
        return
    if not args.private_key or not args.manifest:
        parser.error("Use --generate or provide --private-key and --manifest")
    sign(Path(args.private_key), Path(args.manifest))


if __name__ == "__main__":
    main()
