import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { useAuth } from '../context/AuthContext';
import toast from 'react-hot-toast';
import './AuthPages.css';

interface RegisterForm {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
}

export default function RegisterPage() {
  const { register: registerUser } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const { register, handleSubmit } = useForm<RegisterForm>();

  const onSubmit = async (data: RegisterForm) => {
    setLoading(true);
    try {
      await registerUser(data);
      toast.success('Account created!');
      navigate('/dashboard');
    } catch {
      toast.error('Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="auth-header">
          <h1>Create Account</h1>
          <p>Register as a student to explore events</p>
        </div>
        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="grid grid-2">
            <div className="form-group">
              <label>First Name</label>
              <input {...register('firstName', { required: true })} />
            </div>
            <div className="form-group">
              <label>Last Name</label>
              <input {...register('lastName', { required: true })} />
            </div>
          </div>
          <div className="form-group">
            <label>Email</label>
            <input type="email" {...register('email', { required: true })} />
          </div>
          <div className="form-group">
            <label>Password</label>
            <input type="password" {...register('password', { required: true, minLength: 6 })} />
          </div>
          <button type="submit" className="btn btn-primary auth-btn" disabled={loading}>
            {loading ? 'Creating...' : 'Register'}
          </button>
        </form>
        <p className="auth-footer">
          Already have an account? <Link to="/login">Sign In</Link>
        </p>
      </div>
    </div>
  );
}
