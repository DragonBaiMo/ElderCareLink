#!/usr/bin/env python3
"""
生成 JWT Base64 秘钥与默认登录密码的 BCrypt 哈希，保证凭据来自可复现的 Python 算法。
运行后会在 scripts/generated_secrets.json 中输出结果，并打印到控制台。
"""
import base64
import json
import secrets
import os

try:
    import bcrypt
except ImportError as exc:  # pragma: no cover - 仅用于本地生成
    raise SystemExit("请先运行 `pip install bcrypt` 再执行本脚本") from exc

DEFAULT_PASSWORD = os.getenv("DEFAULT_PASSWORD", "123456")
JWT_BYTES = int(os.getenv("JWT_BYTES", "48"))


def generate_jwt_secret(byte_length: int) -> str:
    """生成指定字节长度的随机秘钥并返回 Base64 字符串。"""
    return base64.b64encode(secrets.token_bytes(byte_length)).decode()


def generate_bcrypt(password: str) -> str:
    """对明文密码生成 BCrypt 哈希。"""
    return bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode()


def main():
    jwt_secret = generate_jwt_secret(JWT_BYTES)
    bcrypt_hash = generate_bcrypt(DEFAULT_PASSWORD)
    payload = {
        "jwt_secret": jwt_secret,
        "default_password": DEFAULT_PASSWORD,
        "bcrypt_hash": bcrypt_hash,
        "byte_length": JWT_BYTES
    }
    output_path = os.path.join(os.path.dirname(__file__), "generated_secrets.json")
    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(payload, f, ensure_ascii=False, indent=2)
    print("生成完毕：")
    print(json.dumps(payload, ensure_ascii=False, indent=2))
    print(f"已写入 {output_path}")


if __name__ == "__main__":
    main()
