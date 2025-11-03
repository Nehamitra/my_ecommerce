import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Form.css";

function SignUp() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: "", // ✅ Only full name
    email: "",
    password: "",
    confirmPassword: "",
  });

  const handleChange = (e) =>
    setFormData({ ...formData, [e.target.name]: e.target.value });

  const validatePassword = (password) => {
    const regex = /^(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$/;
    return regex.test(password);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validatePassword(formData.password)) {
      alert("⚠️ Password must be at least 8 characters long, include 1 number and 1 special character.");
      return;
    }

    if (formData.password !== formData.confirmPassword) {
      alert("⚠️ Passwords do not match!");
      return;
    }

    const requestData = {
      name: formData.name, // ✅ Only send name
      email: formData.email,
      password: formData.password,
    };

    try {
      const response = await fetch("http://localhost:8080/api/auth/signup", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(requestData),
      });

      const text = await response.text();
      const data = text ? JSON.parse(text) : {};

      if (response.ok) {
        if (data.token) {
          localStorage.setItem("token", data.token);
        }
        alert("✅ Signup successful!");
        navigate("/home");
      } else {
        alert("❌ Signup failed: " + (data.message || "Unknown error"));
      }
    } catch (err) {
      console.error("Error:", err);
      alert("❌ Something went wrong during signup!");
    }
  };

  return (
    <div className="form-container">
      <form className="form-card" onSubmit={handleSubmit}>
        <h2>Create Account</h2>

        <input
          type="text"
          name="name"
          placeholder="Full Name"
          value={formData.name}
          onChange={handleChange}
          required
        />

        <input
          type="email"
          name="email"
          placeholder="Email Address"
          value={formData.email}
          onChange={handleChange}
          required
        />

        <input
          type="password"
          name="password"
          placeholder="Password"
          value={formData.password}
          onChange={handleChange}
          required
        />

        <input
          type="password"
          name="confirmPassword"
          placeholder="Confirm Password"
          value={formData.confirmPassword}
          onChange={handleChange}
          required
        />

        <button type="submit">Sign Up</button>

        <p>
          Already have an account?{" "}
          <span className="link" onClick={() => navigate("/")}>
            Sign in
          </span>
        </p>
      </form>
    </div>
  );
}

export default SignUp;