package com.example;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

class Order {
    private final String product;
    private final int quantity;
    private final double price;

    public Order(String product, int quantity, double price) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Order{" +
                "product='" + product + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}

public class CustomExample {
    public static void main(String[] args) {
        List<Order> orders = Arrays.asList(
                new Order("Product A", 2, 50.0),
                new Order("Product B", 1, 30.0),
                new Order("Product A", 1, 50.0),
                new Order("Product C", 3, 20.0)
        );


        Flux.fromIterable(orders)
                .filter(order -> "Product A".equals(order.getProduct())) // Filtrar por Producto A
                .map(order -> order.getQuantity() * order.getPrice()) // Calcular el total por pedido
                .reduce(Double::sum) // Calcular el total de ventas para Producto A
                .subscribe(total -> System.out.println("Total sales for Product A: " + total));


        Flux<Order> ordersFlux = Flux.fromIterable(orders); //Obtiene el iterable
        Flux<Order> filteredOrders = ordersFlux.filter(order -> "Product A".equals(order.getProduct())); //Filtra por producto A
        Flux<Double> totalPerOrder = filteredOrders.map(order -> order.getQuantity() * order.getPrice()); // Calcula el total por pedido de Producto A
        Mono<Double> totalSalesProductA = totalPerOrder.reduce(Double::sum); // Calcula el total de ventas

        //flatMap permite simular una operación asincrónica
        Flux<String> productDetailsFlux = filteredOrders.flatMap(order -> getProductDetails(order.getProduct()));

        //merge combina dos flujos, en este caso se escoge producto A y producto C
        Flux<Order> ordersForProductAAndC = ordersFlux.filter(order -> "Product A".equals(order.getProduct()) || "Product C".equals(order.getProduct()));

        // zip para combinar resultados de dos flujos
            Flux<Double> totalSalesProductC = ordersFlux.filter(order -> "Product C".equals(order.getProduct()))
                    .map(order -> order.getQuantity() * order.getPrice())
                    .reduce(Double::sum)
                    .flux(); // se convierte mono en flux

            Flux<String> combinedResults = Flux.zip(totalSalesProductA.flux(), totalSalesProductC,
                    (totalA, totalC) -> String.valueOf("Total sales for Product A: " + totalA + ", Total sales for Product C: " + totalC));


            combinedResults.subscribe(System.out::println);
            ordersForProductAAndC.subscribe(order -> System.out.println("Filtered Order: " + order));

    }
        private static Mono<String> getProductDetails(String product) {
        return Mono.just("Details of " + product);
        }

}



