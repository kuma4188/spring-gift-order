function updateOptionQuantity(selectElement) {
    const wishlistId = selectElement.getAttribute('data-wishlist-id');
    const optionId = selectElement.getAttribute('data-option-id');
    const quantity = selectElement.value;

    fetch(`/web/wishlist/update/${wishlistId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({quantity: quantity, optionId: optionId})
    })
    .then(response => response.json())
    .then(data => {
        console.log('Option quantity updated.');
    })
    .catch(error => console.error('Error:', error));
}

function refreshPage() {
    window.location.reload();
}

function deleteWishlistItem(button) {
    const wishlistId = button.getAttribute('data-wishlist-id');
    fetch(`/web/wishlist/delete/${wishlistId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    })
    .then(response => response.text())
    .then(data => {
        alert(data);
        window.location.reload();
    })
    .catch(error => console.error('Error:', error));
}

document.addEventListener("DOMContentLoaded", function() {
    document.querySelectorAll('.delete-wishlist').forEach(function(button) {
        button.addEventListener('click', function() {
            deleteWishlistItem(this);
        });
    });
    document.querySelectorAll('.update-quantity').forEach(function(button) {
        button.addEventListener('click', function() {
            refreshPage();
        });
    });
    document.querySelectorAll('.option-quantity').forEach(select => {
        select.addEventListener('change', function() {
            updateOptionQuantity(this);
        });
    });
});
