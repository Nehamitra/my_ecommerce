import React, { useEffect, useState, useCallback, useRef } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import './Home.css';


function Home() {
  const [categories, setCategories] = useState([]);
  const [products, setProducts] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [searchResults, setSearchResults] = useState({ products: [], categories: [] });
  const [loading, setLoading] = useState(false);
  const [searchError, setSearchError] = useState(null);
  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const searchTimeoutRef = useRef(null);

  useEffect(() => {
    if (!token) navigate("/");
  }, [token, navigate]);

  // Custom debounce function
  const debounce = (func, delay) => {
    return (...args) => {
      if (searchTimeoutRef.current) {
        clearTimeout(searchTimeoutRef.current);
      }
      searchTimeoutRef.current = setTimeout(() => func(...args), delay);
    };
  };

  // Search function
  const performSearch = async (query) => {
    if (query.trim() === "") {
      setSearchResults({ products: [], categories: [] });
      setLoading(false);
      return;
    }

    setLoading(true);
    setSearchError(null);
    
    try {
      console.log("🔍 Searching for:", query);
      
      const res = await axios.get(`http://localhost:8080/api/search/global?keyword=${query}`, {
        headers: { 
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      });
      
      console.log("📦 Search API Response:", res.data);
      
      setSearchResults({
        products: res.data.products || [],
        categories: res.data.categories || []
      });
    } catch (err) {
      console.error("❌ Error fetching search results", err);
      setSearchError("Failed to perform search. Please try again.");
      setSearchResults({ products: [], categories: [] });
    } finally {
      setLoading(false);
    }
  };

  // Debounced search function
  const debouncedSearch = useCallback(
    debounce((query) => {
      performSearch(query);
    }, 300),
    [token]
  );

  // Handle search input change
  const handleSearchChange = (e) => {
    const value = e.target.value;
    setSearchTerm(value);
    setLoading(true);
    debouncedSearch(value);
  };

  // Cleanup timeout on unmount
  useEffect(() => {
    return () => {
      if (searchTimeoutRef.current) {
        clearTimeout(searchTimeoutRef.current);
      }
    };
  }, []);

  useEffect(() => {
    if (!token) return;

    // Fetch categories
    axios
      .get("http://localhost:8080/api/categories", {
        headers: { 
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      })
      .then((res) => setCategories(res.data))
      .catch((err) => {
        console.error("Error fetching categories", err);
        if (err.response?.status === 403 || err.response?.status === 401) {
          localStorage.removeItem("token");
          navigate("/");
        }
      });

    // Fetch products
    axios
      .get("http://localhost:8080/api/products", {
        headers: { 
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      })
      .then((res) => setProducts(res.data))
      .catch((err) => {
        console.error("Error fetching products", err);
        if (err.response?.status === 403 || err.response?.status === 401) {
          localStorage.removeItem("token");
          navigate("/");
        }
      });
  }, [token, navigate]);

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/");
  };

  // ✅ Use search results when searching, otherwise use normal data
  const displayCategories = searchTerm ? searchResults.categories : categories;
  const displayProducts = searchTerm ? searchResults.products : products;

  return (
    <div style={{ fontFamily: "Segoe UI, sans-serif", backgroundColor: "#f8f9fa", minHeight: "100vh" }}>
      {/* Navbar */}
      <nav style={{
        backgroundColor: "#ffffff",
        padding: "10px 20px",
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        boxShadow: "0 2px 6px rgba(0,0,0,0.1)",
        position: "sticky",
        top: 0,
        zIndex: 1000
      }}>
        <div style={{ fontSize: "24px", fontWeight: "bold", color: "#2a2a2a" }}>🛒 MyShop</div>
        
        {/* Search Input with Loading Indicator */}
        <div style={{ position: "relative", width: "40%" }}>
          <input
            type="text"
            placeholder="Search categories or products..."
            value={searchTerm}
            onChange={handleSearchChange}
            style={{
              padding: "8px 12px",
              borderRadius: "20px",
              border: "1px solid #ccc",
              width: "100%",
              outline: "none",
              paddingRight: "40px" // Space for loading indicator
            }}
          />
          {loading && (
            <div style={{
              position: "absolute",
              right: "12px",
              top: "50%",
              transform: "translateY(-50%)",
              color: "#007bff",
              fontSize: "12px"
            }}>
              Searching...
            </div>
          )}
        </div>

        {searchError && (
          <div style={{
            position: "absolute",
            top: "100%",
            left: "50%",
            transform: "translateX(-50%)",
            backgroundColor: "#f8d7da",
            color: "#721c24",
            padding: "8px 12px",
            borderRadius: "4px",
            fontSize: "12px",
            marginTop: "4px",
            border: "1px solid #f5c6cb"
          }}>
            {searchError}
          </div>
        )}
        
        <div style={{ display: "flex", gap: "10px" }}>
          <button
            onClick={() => navigate("/all-products")}
            style={{
              padding: "8px 16px",
              backgroundColor: "#007bff",
              color: "#fff",
              border: "none",
              borderRadius: "20px",
              fontWeight: "bold",
              cursor: "pointer"
            }}
          >
            All Products
          </button>
          <button
            onClick={handleLogout}
            style={{
              padding: "8px 16px",
              backgroundColor: "#dc3545",
              color: "#fff",
              border: "none",
              borderRadius: "20px",
              fontWeight: "bold",
              cursor: "pointer"
            }}
          >
            Logout
          </button>
        </div>
      </nav>

      {/* Main content */}
      <div style={{ padding: "40px 20px", maxWidth: "1200px", margin: "auto" }}>
        {/* Categories */}
        {displayCategories.length > 0 && (
          <>
            <h2 style={{ marginBottom: "20px", color: "#333" }}>
              {searchTerm ? "Search Results - Categories" : "Categories"}
            </h2>
            <div style={{
              display: "grid",
              gridTemplateColumns: "repeat(auto-fit, minmax(250px, 1fr))",
              gap: "30px"
            }}>
              {displayCategories.map((category) => (
                <div
                  key={category.id}
                  onClick={() => navigate(`/products/${category.id}`)}
                  style={{
                    backgroundColor: "#fff",
                    borderRadius: "12px",
                    overflow: "hidden",
                    boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
                    cursor: "pointer",
                    transition: "transform 0.2s",
                    ...(searchTerm ? {
                      maxWidth: "250px",
                      margin: "0 auto"
                    } : {})
                  }}
                  onMouseEnter={(e) => e.currentTarget.style.transform = "scale(1.03)"}
                  onMouseLeave={(e) => e.currentTarget.style.transform = "scale(1)"}
                >
                  {/* REPLACED IMAGE WITH SIMPLE DIV */}
                  <div style={{ 
                    width: "100%", 
                    height: "140px", 
                    backgroundColor: "#e9ecef",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    borderBottom: "1px solid #dee2e6"
                  }}>
                    <span style={{ color: "#6c757d", fontSize: "14px" }}>No Image</span>
                  </div>
                  
                  <div style={{ padding: "12px", fontSize: "16px", fontWeight: "600", textAlign: "center", color: "#333" }}>
                    {category.name}
                  </div>
                </div>
              ))}
            </div>
          </>
        )}

        {/* Products */}
        {searchTerm && displayProducts.length > 0 && (
          <>
            <h2 style={{ margin: "40px 0 20px", color: "#333" }}>Search Results - Products</h2>
            <div style={{
              display: "grid",
              gridTemplateColumns: "repeat(auto-fit, minmax(250px, 1fr))",
              gap: "30px"
            }}>
              {displayProducts.map((product) => (
                <div
                  key={product.id || product.productId}
                  className="col-md-3 mb-4"
                  onClick={() => navigate(`/product/${product.id || product.productId}`)}
                  style={{ 
                    cursor: 'pointer',
                    backgroundColor: "#fff",
                    borderRadius: "12px",
                    overflow: "hidden",
                    boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
                    transition: "transform 0.2s"
                  }}
                  onMouseEnter={(e) => e.currentTarget.style.transform = "scale(1.03)"}
                  onMouseLeave={(e) => e.currentTarget.style.transform = "scale(1)"}
                >
                  <div className="card h-100" style={{ border: "none" }}>
                    {/* REPLACED IMAGE WITH SIMPLE DIV */}
                    <div style={{ 
                      width: "100%", 
                      height: "150px", 
                      backgroundColor: "#f8f9fa",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      borderBottom: "1px solid #dee2e6"
                    }}>
                      <span style={{ color: "#6c757d", fontSize: "14px" }}>Product Image</span>
                    </div>
                    
                    <div className="card-body">
                      <h5 className="card-title">{product.name}</h5>
                      <p className="card-text">₹{product.price}</p>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </>
        )}

        {/* Show default products when not searching */}
        {!searchTerm && displayProducts.length > 0 && (
          <>
            <h2 style={{ margin: "40px 0 20px", color: "#333" }}>Featured Products</h2>
            <div style={{
              display: "grid",
              gridTemplateColumns: "repeat(auto-fit, minmax(250px, 1fr))",
              gap: "30px"
            }}>
              {displayProducts.slice(0, 8).map((product) => (
                <div
                  key={product.id || product.productId}
                  className="col-md-3 mb-4"
                  onClick={() => navigate(`/product/${product.id || product.productId}`)}
                  style={{ 
                    cursor: 'pointer',
                    backgroundColor: "#fff",
                    borderRadius: "12px",
                    overflow: "hidden",
                    boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
                    transition: "transform 0.2s"
                  }}
                  onMouseEnter={(e) => e.currentTarget.style.transform = "scale(1.03)"}
                  onMouseLeave={(e) => e.currentTarget.style.transform = "scale(1)"}
                >
                  <div className="card h-100" style={{ border: "none" }}>
                    {/* REPLACED IMAGE WITH SIMPLE DIV */}
                    <div style={{ 
                      width: "100%", 
                      height: "150px", 
                      backgroundColor: "#f8f9fa",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      borderBottom: "1px solid #dee2e6"
                    }}>
                      <span style={{ color: "#6c757d", fontSize: "14px" }}>Product Image</span>
                    </div>
                    
                    <div className="card-body">
                      <h5 className="card-title">{product.name}</h5>
                      <p className="card-text">₹{product.price}</p>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </>
        )}

        {/* Loading State */}
        {loading && displayCategories.length === 0 && displayProducts.length === 0 && (
          <div style={{ textAlign: "center", color: "#007bff", fontSize: "18px", marginTop: "60px" }}>
            Searching... Please wait.
          </div>
        )}

        {/* No Results */}
        {searchTerm && !loading && displayCategories.length === 0 && displayProducts.length === 0 && (
          <div style={{ textAlign: "center", color: "gray", fontSize: "18px", marginTop: "60px" }}>
            No matching categories or products found.
          </div>
        )}

        {/* Welcome message when no data */}
        {!searchTerm && !loading && displayCategories.length === 0 && displayProducts.length === 0 && (
          <div style={{ textAlign: "center", color: "gray", fontSize: "18px", marginTop: "60px" }}>
            Welcome to MyShop! Categories and products will appear here.
          </div>
        )}
      </div>
    </div>
  );
}

export default Home;