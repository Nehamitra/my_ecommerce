import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import SignIn from "./pages/SignIn";
import SignUp from "./pages/SignUp";
import Home from "./pages/Home";
import Product from "./pages/Product"; 
import ProductDetails from "./pages/ProductDetails"; // ✅ added
import AllProducts from "./pages/AllProducts"; // ✅ added
import "./App.css";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<SignIn />} />
        <Route path="/signup" element={<SignUp />} />
        <Route path="/home" element={<Home />} />
        <Route path="/products/:categoryId" element={<Product />} /> {/* ✅ added */}
          <Route path="/product/:id" element={<ProductDetails />} />
          <Route path="/all-products" element={<AllProducts />} /> 
      </Routes>
    </Router>
  );
}

export default App;
