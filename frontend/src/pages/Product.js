import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './Product.css';
import { useNavigate, useParams } from 'react-router-dom';

const Product = () => {
  const [products, setProducts] = useState([]);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const { categoryId } = useParams();
  const navigate = useNavigate();

  const token = localStorage.getItem('token');

  const fetchProducts = async () => {
    try {
      const response = await axios.get(`http://localhost:8080/api/products/${categoryId}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        params: {
          page,
          size: 4,
          search,
        },
      });

      const data = response.data;
      console.log("Fetched product data:", data);

      if (data && Array.isArray(data.content)) {
        setProducts(data.content);
        setTotalPages(data.totalPages || 1);
      } else if (Array.isArray(data)) {
        // In case the backend just returns an array (no pagination)
        setProducts(data);
        setTotalPages(1);
      } else {
        setProducts([]);
        setTotalPages(1);
      }

    } catch (error) {
      console.error('Error fetching products:', error);
      setProducts([]);
    }
  };

  useEffect(() => {
    if (categoryId && token) {
      fetchProducts();
    }
  }, [categoryId, page, search, token]);

  const handleSearchChange = (e) => {
    setSearch(e.target.value);
    setPage(0); // reset to first page on new search
  };

  return (
    <div className="product-container">
      <div className="header">
  <div className="header-top">
    <h2 className="product-title">Products</h2>
    <button className="go-back-btn" onClick={() => navigate("/home")}>
      ← Go to Categories
    </button>
  </div>
  <input
    type="text"
    className="search-input"
    placeholder="Search products..."
    value={search}
    onChange={handleSearchChange}
  />
</div>



      <div className="product-grid">
        {Array.isArray(products) && products.length > 0 ? (
          products.map((product) => (
            <div
              className="product-card"
              key={product.id}
              onClick={() => navigate(`/product/${product.id}`)}
              style={{ cursor: 'pointer' }}
            >
              <img src={product.picture} alt={product.name} />
              <h3>{product.name}</h3>
              <p>₹{product.price}</p>
              <p>{product.description}</p>
              <p>In stock: {product.quantity}</p>
            </div>
          ))
        ) : (
          <p style={{ textAlign: "center", width: "100%" }}>No products found.</p>
        )}
      </div>

      <div className="pagination">
        <button onClick={() => setPage((p) => Math.max(p - 1, 0))} disabled={page === 0}>
          &lt;
        </button>
        {[...Array(totalPages).keys()].map((pg) => (
          <button
            key={pg}
            onClick={() => setPage(pg)}
            className={pg === page ? 'active' : ''}
          >
            {pg + 1}
          </button>
        ))}
        <button
          onClick={() => setPage((p) => Math.min(p + 1, totalPages - 1))}
          disabled={page === totalPages - 1}
        >
          &gt;
        </button>
      </div>
    </div>
  );
};

export default Product;

