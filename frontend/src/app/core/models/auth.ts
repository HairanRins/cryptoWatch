export interface AuthResponse {
  success: boolean;
  message: string;
  data: {
    token: string;
    user: User;
  } | null;
  timestamp: string;
}

export interface User {
  id: string;
  email: string;
  name: string;
  avatarUrl: string | null;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  name: string;
}
