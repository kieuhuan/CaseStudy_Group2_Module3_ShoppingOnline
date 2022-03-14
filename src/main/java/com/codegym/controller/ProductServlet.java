package com.codegym.controller;

import com.codegym.model.Category;
import com.codegym.model.Product;
import com.codegym.model.User;
import com.codegym.service.CategoryService;
import com.codegym.service.OrderService;
import com.codegym.service.ProductService;
import com.codegym.service.UserService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ProductServlet", value = "/products")
public class ProductServlet extends HttpServlet {
    ProductService productService = new ProductService();
    CategoryService categoryService = new CategoryService();
    UserService userService = new UserService();
    OrderService orderService = new OrderService();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = request.getParameter("action");
        if(action == null){
            action = "";
        }
        switch (action){
            case "view": {
                showProductDetail(request, response);
                break;
            }
            case "create":{
                showCreateForm(request, response);
                break;
            }
            case "edit":{
                showEditForm(request, response);
                break;
            }
            case "delete":{
                showDeleteForm(request, response);
                break;
            }
            case "showHome":
            {
               showAdminHome(request, response);
                break;
            }
            default:{
                showListProduct(request, response);
            }
        }
    }
    void showAdminHome(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user =(User) session.getAttribute("user");
        request.setAttribute("username",user.getName());
        int countUser = userService.countUser();
        request.setAttribute("countUser",countUser);
        int countOrder = orderService.countOrder();
        request.setAttribute("countOrder",countOrder);
        int totalProductQuantity = orderService.totalQuantityOfProduct();
        request.setAttribute("totalProductQuantity",totalProductQuantity);
        Double totalAvenue = orderService.totalRevenue();
        request.setAttribute("totalAvenue",totalAvenue);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/adminTemplate/index.jsp");
        dispatcher.forward(request, response);

    };

    private void showDeleteForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user =(User) session.getAttribute("user");
        request.setAttribute("username",user.getName());
        int id = Integer.parseInt(request.getParameter("id"));
        Product product = productService.findByID(id);
        request.setAttribute("product",product);
        Category category = categoryService.findByID(product.getCategory_id());
        request.setAttribute("category",category);
        List<Category> categories = categoryService.findAll();
        request.setAttribute("categories",categories);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/adminTemplate/product/delete.jsp");
        dispatcher.forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user =(User) session.getAttribute("user");
        request.setAttribute("username",user.getName());
        List<Category> categories = categoryService.findAll();
        request.setAttribute("categories",categories);
        int id = Integer.parseInt(request.getParameter("id"));
        Product product = productService.findByID(id);
        request.setAttribute("product",product);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/adminTemplate/product/edit.jsp");
        dispatcher.forward(request, response);
    }

    private void showCreateForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user =(User) session.getAttribute("user");
        request.setAttribute("username",user.getName());
        List<Category> categories = categoryService.findAll();
        request.setAttribute("categories",categories);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/adminTemplate/product/create.jsp");
        dispatcher.forward(request, response);
    }

    private void showProductDetail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user =(User) session.getAttribute("user");
        request.setAttribute("username",user.getName());
        int id = Integer.parseInt(request.getParameter("id"));
        Product product = productService.findByID(id);
        Category category = categoryService.findByID(product.getCategory_id());
        request.setAttribute("product",product);
        request.setAttribute("category",category);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/adminTemplate/product/view.jsp");
        dispatcher.forward(request, response);
    }

    private void showListProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user =(User) session.getAttribute("user");
        request.setAttribute("username",user.getName());
        String q = request.getParameter("q");
        if (q == null) {
            List<Product> products = productService.findAll();
            request.setAttribute("products", products);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/adminTemplate/product/list.jsp");
            dispatcher.forward(request, response);
        } else {
            List<Product> products = productService.searchByName(q);
            request.setAttribute("products", products);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/adminTemplate/product/list.jsp");
            dispatcher.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String action = request.getParameter("action");
        if(action == null){
            action = "";
        }
        switch (action){
            case "create":{
                createNewProduct(request, response);
                break;
            }
            case "edit":{
                editProduct(request, response);
                break;
            }
            case "delete":{
                deleteProduct(request, response);
                break;
            }
        }
    }

    private void deleteProduct(HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean isUpdated = productService.delete(id);
        String message;
        if(isUpdated){
            message = "Successfully deleted!";
        } else {
            message = "Delete failed";
        }
        request.setAttribute("message",message);
        request.setAttribute("isUpdated",isUpdated);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/adminTemplate/product/delete.jsp");
        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void editProduct(HttpServletRequest request, HttpServletResponse response) {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        Double price = Double.valueOf(request.getParameter("price"));
        String description = request.getParameter("description");
        int category_id = Integer.parseInt(request.getParameter("category_id"));
        String image = request.getParameter("image");
        Product product = new Product(name,price,description,category_id,image);
        boolean isUpdated = productService.update(id,product);
        String message;
        if(isUpdated){
            message = "Successfully edited!";
        } else {
            message = "Edit failed";
        }
        request.setAttribute("message",message);
        request.setAttribute("isUpdated",isUpdated);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/adminTemplate/product/edit.jsp");
        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNewProduct(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        Double price = Double.valueOf(request.getParameter("price"));
        String description = request.getParameter("description");
        int category_id = Integer.parseInt(request.getParameter("category_id"));
        String image = request.getParameter("image");
        Product product = new Product(name,price,description,category_id,image);
        boolean isUpdated = productService.create(product);
        String message;
        if(isUpdated){
            message = "Created successfully!";
        } else {
            message = "Created failed!";
        }
        request.setAttribute("message",message);
        request.setAttribute("isUpdated",isUpdated);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/adminTemplate/product/create.jsp");
        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
