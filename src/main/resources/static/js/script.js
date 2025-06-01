const toggleSideBar = () => {
    console.log("hello from toggle function");
    if($(".sidebar").is(":visible")){
        $(".sidebar").css("display","none");
        $(".content").css("margin-left","0%");
    }else{
        $(".sidebar").css("display","block");
        $(".content").css("margin-left","19%");
    }
};

const donate = ()=>{
    console.log("payment started...");
    let amount = $("#amount").val();
    console.log(amount);
    if(amount===""||amount==null){
        alert("Please enter amount!");
        return;
    }
    $.ajax({
        url: '/user/create_order',
        data: JSON.stringify({ amount: amount,info:'order_req'}),
        contentType: 'application/json',
        type: 'POST',
        dataType: 'json',
        success: function (response) {
            // handle success
            console.log(response);
            if(response.status==="CREATED"){
                console.log("OKOK");
                $("#payment").html('<div class="alert alert-success">Payment Successful! Order ID: '+response.id+
                                    '</div>');
            }
        },
        error: function (error) {
            // handle error
            console.log("ERROR"+error);
            alert("Something went wrong!")
        }
    });

};