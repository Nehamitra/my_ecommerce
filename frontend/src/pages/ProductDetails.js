import React, { useEffect, useState, useCallback } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";

function ProductDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [product, setProduct] = useState(null);
  const token = localStorage.getItem("token");

  const fetchProduct = useCallback(() => {
    if (!token) {
      navigate("/login");
      return;
    }

    axios.get(`http://localhost:8080/api/products/details/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => setProduct(res.data))
      .catch((err) => console.error("Error loading product details", err));
  }, [id, navigate, token]);

  useEffect(() => {
    fetchProduct();
  }, [fetchProduct]);

  if (!product) return <div style={{ padding: "2rem" }}>Loading...</div>;

  return (
    <div style={{ padding: "2rem", fontFamily: "Segoe UI, sans-serif" }}>
      <div
        style={{
          display: "flex",
          gap: "2rem",
          flexWrap: "wrap",
          background: "#fff",
          boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
          borderRadius: "12px",
          padding: "2rem",
        }}
      >
        <img
          src={product.picture}
          alt={product.name}
          style={{ width: "300px", borderRadius: "8px" }}
        />
        <div>
          <h2 style={{ fontSize: "28px", marginBottom: "1rem" }}>{product.name}</h2>
          <p style={{ fontSize: "20px", color: "#333" }}>{product.description}</p>
          <p style={{ fontSize: "24px", color: "#007bff", margin: "1rem 0" }}>
            ₹{product.price}
          </p>
          <p style={{ fontSize: "18px", color: "#666" }}>
            In stock: {product.quantity}
          </p>
        </div>
      </div>
    </div>
  );
}

export default ProductDetails;
