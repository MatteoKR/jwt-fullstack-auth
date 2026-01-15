export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string; // il token JWT restituito dal backend
}

export interface RegisterRequest {
  username: string;
  password: string;
}