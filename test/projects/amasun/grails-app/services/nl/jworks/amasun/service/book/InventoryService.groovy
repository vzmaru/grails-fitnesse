package nl.jworks.amasun.service.book

import nl.jworks.amasun.domain.book.Book
import nl.jworks.amasun.domain.book.Inventory
import nl.jworks.amasun.domain.book.PromoPackage
import nl.jworks.amasun.domain.order.Order

class InventoryService {

    void addBooks(Book book, int delta) {
        Inventory inventory = findByAuthorAndTitle(book.author, book.title)

        if(!inventory) {
            inventory = new Inventory(book:book)
        }
        
        inventory.incrementAmount(delta)
        inventory.save()
    }

    Book findByTitle(String title) {
        Book.findByTitle(title)
    }

    Integer getAmount(Book book) {
        Inventory inventory = findByAuthorAndTitle(book.author, book.title)
        if(inventory) {
            return inventory.amount
        } else {
            return 0
        }
    }

    void deductInventory(Book book, Integer amount) {
        def inventory = findByAuthorAndTitle(book.author, book.title)

        if(inventory.amount - amount < 0) {
            throw new IllegalArgumentException("Not enough books in stock: current stock is ${inventory.amount}, trying to deduct ${amount}")
        }

        inventory.amount = inventory.amount - amount
        inventory.save()
    }

    List<Inventory> checkInventory() {
        return Inventory.list()
    }


    private Inventory findByAuthorAndTitle(author, title) {
        Inventory.find("from Inventory as i where i.book.author = :author and i.book.title = :title", [author:author, title:title])
    }

    void addPromoPackage(PromoPackage promoPackage) {
        promoPackage.save()
    }

    PromoPackage getPromoPackage(Order order) {
        PromoPackage.list().find { promopackage -> promopackage.isEligible(order) }
    }

    boolean isPromoOrder(Order order) {
        return getPromoPackage(order) != null
    }
}