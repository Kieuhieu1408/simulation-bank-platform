# Cloudflare Tunnel Setup

To expose the local stack to the internet securely, you can use Cloudflare Tunnel.

## Prerequisites
- A Cloudflare account
- `cloudflared` CLI installed

## Setup Instructions

1. **Login to Cloudflare:**
   ```bash
   cloudflared tunnel login
   ```

2. **Create a Tunnel:**
   ```bash
   cloudflared tunnel create sim-bank
   ```

3. **Configure the Tunnel:**
   Create or edit the config file (usually `~/.cloudflared/config.yml`):
   ```yaml
   url: http://localhost:80
   tunnel: <TUNNEL_ID>
   credentials-file: /root/.cloudflared/<TUNNEL_ID>.json
   ```
   *(Note: The `url` should point to the Nginx Edge Load Balancer)*

4. **Route Traffic:**
   Route DNS to your tunnel:
   ```bash
   cloudflared tunnel route dns sim-bank api.yourdomain.com
   ```

5. **Run the Tunnel:**
   ```bash
   cloudflared tunnel run sim-bank
   ```

## Security
Configure Zero Trust Access policies in your Cloudflare dashboard to protect endpoints.
