import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const AllProducts = () => {
  const [products, setProducts] = useState([]);
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  useEffect(() => {
    if (!token) navigate("/login");
    axios
      .get("http://localhost:8080/api/products", {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => setProducts(res.data))
      .catch((err) => console.error("Error fetching products", err));
  }, [token, navigate]);

  return (
    <div style={{ padding: "40px 20px", maxWidth: "1200px", margin: "auto" }}>
      <h2 style={{ marginBottom: "30px", textAlign: "center" }}>All Products</h2>
      <div style={{
        display: "grid",
        gridTemplateColumns: "repeat(auto-fit, minmax(250px, 1fr))",
        gap: "30px"
      }}>
        {products.map((product) => (
          <div
            key={product.productId}
            onClick={() => navigate(`/product/${product.productId}`)}
            style={{
              backgroundColor: "#fff",
              borderRadius: "12px",
              boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
              cursor: "pointer",
              overflow: "hidden",
              transition: "transform 0.2s"
            }}
            onMouseEnter={(e) => e.currentTarget.style.transform = "scale(1.03)"}
            onMouseLeave={(e) => e.currentTarget.style.transform = "scale(1)"}
          >
            <img
              src={product.image}
              alt={product.name}
              style={{ width: "100%", height: "160px", objectFit: "cover" }}
            />
            <div style={{ padding: "12px", fontSize: "16px", fontWeight: "600", textAlign: "center" }}>
              {product.name} <br />
              <span style={{ color: "#28a745" }}>₹{product.price}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default AllProducts;
