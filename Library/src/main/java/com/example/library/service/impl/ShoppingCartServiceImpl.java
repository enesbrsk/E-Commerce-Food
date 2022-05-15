package com.example.library.service.impl;

import com.example.library.dto.CartItemDto;
import com.example.library.dto.ProductDto;
import com.example.library.dto.ShoppingCartDto;
import com.example.library.model.CartItem;
import com.example.library.model.Customer;
import com.example.library.model.Product;
import com.example.library.model.ShoppingCart;
import com.example.library.repository.CartItemRepository;
import com.example.library.repository.ShoppingCartRepository;
import com.example.library.service.CustomerService;
import com.example.library.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final CustomerService customerService;

    public ShoppingCartServiceImpl(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public ShoppingCart addItemToCart(ProductDto productDto, int quantity, String username) {
        Customer customer = customerService.findByUsername(username);
        ShoppingCart shoppingCart = customer.getCart();

        if(shoppingCart == null){
            shoppingCart = new ShoppingCart();
        }
        Set<CartItem> cartItemList = shoppingCart.getCartItems();
        CartItem cartItem = find(cartItemList, productDto.getId());
        Product product = transfer(productDto);

        double unitPrice = productDto.getCostPrice();

        int itemQuantity = 0;
        if(cartItemList == null){
            cartItemList = new HashSet<>();
            if (cartItem == null) {
                cartItem = new CartItem();
                cartItem.setProduct(product);
                cartItem.setCart(shoppingCart);
                cartItem.setQuantity(quantity);
                cartItem.setUnitPrice(unitPrice);
                cartItem.setCart(shoppingCart);
                cartItemList.add(cartItem);
            } else {
                itemQuantity = cartItem.getQuantity() + quantity;
                cartItem.setQuantity(itemQuantity);

            }
        }else{
            if(cartItem == null){
                cartItem = new CartItem();
                cartItem.setProduct(product);
                cartItem.setCart(shoppingCart);
                cartItem.setQuantity(quantity);
                cartItem.setUnitPrice(unitPrice);
                cartItem.setCart(shoppingCart);
                cartItemList.add(cartItem);
            }else{
                itemQuantity = cartItem.getQuantity() + quantity;
                cartItem.setQuantity(itemQuantity);
            }
        }
        shoppingCart.setCartItems(cartItemList);

        double totalPrice = totalPrice(cartItemList);
        int totalItem = totalItem(cartItemList);

        shoppingCart.setTotalPrice(totalPrice);
        shoppingCart.setTotalItems(totalItem);
        return shoppingCart;
    }

    @Override
    public ShoppingCart updateCart(ProductDto productDto, int quantity, String username) {
        Customer customer = customerService.findByUsername(username);
        ShoppingCart shoppingCart = customer.getCart();
        Set<CartItem> cartItemList = shoppingCart.getCartItems();

        CartItem item = find(cartItemList, productDto.getId());

        int itemQuantity = item.getQuantity() + quantity;
        int totalItem = totalItem(cartItemList);
        double totalPrice = totalPrice(cartItemList);
        item.setQuantity(itemQuantity);
        shoppingCart.setCartItems(cartItemList);
        shoppingCart.setTotalPrice(totalPrice);
        shoppingCart.setTotalItems(totalItem);
        return shoppingCart;
    }

    @Override
    public ShoppingCart removeItemFromCart(ProductDto productDto, String username) {
        Customer customer = customerService.findByUsername(username);
        ShoppingCart shoppingCart = customer.getCart();
        Set<CartItem> cartItemList = shoppingCart.getCartItems();
        CartItem item = find(cartItemList, productDto.getId());
        cartItemList.remove(item);
        double totalPrice = totalPrice(cartItemList);
        int totalItem = totalItem(cartItemList);
        shoppingCart.setCartItems(cartItemList);
        shoppingCart.setTotalPrice(totalPrice);
        shoppingCart.setTotalItems(totalItem);
        return shoppingCart;
    }

    @Override
    public ShoppingCartDto addItemToCartSession(ShoppingCartDto cartDto, ProductDto productDto, int quantity) {
        CartItemDto cartItem = findInDTO(cartDto, productDto.getId());
        if(cartDto == null){
            cartDto = new ShoppingCartDto();
        }
        Set<CartItemDto> cartItemList = cartDto.getCartItems();
        double unitPrice = productDto.getCostPrice();
        int itemQuantity = 0;
        if(cartItemList == null){
            cartItemList = new HashSet<>();
            if(cartItem == null){
                cartItem = new CartItemDto();
                cartItem.setProduct(productDto);
                cartItem.setCart(cartDto);
                cartItem.setQuantity(quantity);
                cartItem.setUnitPrice(unitPrice);
                cartItemList.add(cartItem);
            }else{
                itemQuantity = cartItem.getQuantity() + quantity;
                cartItem.setQuantity(itemQuantity);
            }
        }else{
            if(cartItem == null){
                cartItem = new CartItemDto();
                cartItem.setProduct(productDto);
                cartItem.setCart(cartDto);
                cartItem.setQuantity(quantity);
                cartItem.setUnitPrice(unitPrice);
                cartItemList.add(cartItem);
            }else{
                itemQuantity = cartItem.getQuantity() + quantity;
                cartItem.setQuantity(itemQuantity);
            }
        }
        cartDto.setCartItems(cartItemList);
        double totalPrice = totalPriceDto(cartItemList);
        int totalItem = totalItemDto(cartItemList);
        cartDto.setTotalPrice(totalPrice);
        cartDto.setTotalItems(totalItem);
        return cartDto;
    }

    @Override
    public ShoppingCartDto updateCartSession(ShoppingCartDto cartDto,ProductDto productDto, int quantity) {
        Set<CartItemDto> cartItemList = cartDto.getCartItems();
        CartItemDto item = findInDTO(cartDto, productDto.getId());
        int itemQuantity = item.getQuantity() + quantity;
        int totalItem = totalItemDto(cartItemList);
        double totalPrice = totalPriceDto(cartItemList);
        item.setQuantity(itemQuantity);
        cartDto.setCartItems(cartItemList);
        cartDto.setTotalPrice(totalPrice);
        cartDto.setTotalItems(totalItem);
        return cartDto;
    }

    @Override
    public ShoppingCartDto removeItemFromCartSession(ShoppingCartDto cartDto,ProductDto productDto, int quantity) {
        Set<CartItemDto> cartItemList = cartDto.getCartItems();
        CartItemDto item = findInDTO(cartDto, productDto.getId());
        cartItemList.remove(item);
        double totalPrice = totalPriceDto(cartItemList);
        int totalItem = totalItemDto(cartItemList);
        cartDto.setCartItems(cartItemList);
        cartDto.setTotalPrice(totalPrice);
        cartDto.setTotalItems(totalItem);
        return cartDto;
    }

    @Override
    public ShoppingCart combineCart(ShoppingCartDto cartDto, ShoppingCart cart) {
        if(cart == null){
            cart = new ShoppingCart();
        }
        Set<CartItem> cartItems = cart.getCartItems();
        if(cartItems == null){
            cartItems = new HashSet<>();
        }
        Set<CartItem> cartItemsDto = convertCartItem(cartDto.getCartItems(), cart);
        for(CartItem cartItem : cartItemsDto){
            cartItems.add(cartItem);
        }
        double totalPrice = totalPrice(cartItems);
        int totalItems = totalItem(cartItems);
        cart.setTotalItems(totalItems);
        cart.setCartItems(cartItems);
        cart.setTotalPrice(totalPrice);
        return cart;
    }

    @Override
    public ShoppingCart getCart(String username) {
        Customer customer = customerService.findByUsername(username);
        ShoppingCart cart = customer.getCart();
        return cart;
    }


    private CartItem find( Set<CartItem> cartItems, long productId) {
        if(cartItems == null){
            return null;
        }
        CartItem cartItem = null;
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == productId) {
                cartItem = item;
            }
        }
        return cartItem;
    }

    private CartItemDto findInDTO(ShoppingCartDto shoppingCart, long productId) {
        if(shoppingCart == null){
            return null;
        }
        CartItemDto cartItem = null;
        for (CartItemDto item : shoppingCart.getCartItems()) {
            if (item.getProduct().getId() == productId) {
                cartItem = item;
            }
        }
        return cartItem;
    }

    private int totalItem(Set<CartItem> cartItemList) {
        int totalItem = 0;
        for (CartItem item : cartItemList) {
            totalItem += item.getQuantity();
        }
        return totalItem;
    }

    private double totalPrice(Set<CartItem> cartItemList) {
        double totalPrice = 0.0;
        for (CartItem item : cartItemList) {
            totalPrice += item.getUnitPrice() * item.getQuantity();
        }
        return totalPrice;
    }

    private int totalItemDto(Set<CartItemDto> cartItemList) {
        int totalItem = 0;
        for (CartItemDto item : cartItemList) {
            totalItem += item.getQuantity();
        }
        return totalItem;
    }

    private double totalPriceDto(Set<CartItemDto> cartItemList) {
        double totalPrice = 0;
        for (CartItemDto item : cartItemList) {
            totalPrice += item.getUnitPrice() * item.getQuantity();
        }
        return totalPrice;
    }

    private Product transfer(ProductDto productDto) {
        Product product = new Product();
        product.setId(productDto.getId());
        product.setName(productDto.getName());
        product.setCurrentQuantity(productDto.getCurrentQuantity());
        product.setCostPrice(productDto.getCostPrice());
        product.setSalePrice(productDto.getSalePrice());
        product.setDescription(productDto.getDescription());
        product.setImage(productDto.getImage());
        product.set_activated(productDto.isActivated());
        product.set_deleted(productDto.isDeleted());
        product.setCategory(productDto.getCategory());
        return product;
    }

    private Set<CartItem> convertCartItem(Set<CartItemDto> cartItemDtos, ShoppingCart cart){
        Set<CartItem> cartItems = new HashSet<>();
        for(CartItemDto cartItemDto : cartItemDtos){
            CartItem cartItem = new CartItem();
            cartItem.setQuantity(cartItemDto.getQuantity());
            cartItem.setProduct(transfer(cartItemDto.getProduct()));
            cartItem.setUnitPrice(cartItemDto.getUnitPrice());
            cartItem.setId(cartItemDto.getId());
            cartItem.setCart(cart);
            cartItems.add(cartItem);
        }
        return cartItems;
    }
}