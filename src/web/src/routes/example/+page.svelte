<script>
    /** @type {HTMLTextAreaElement}*/
    let data
    /** @type {number} */
    let id

    /**
     * @property {number} id
     * @property {stirng} name
     * @property {number} age
     * @property {number} salary
     */
    let newData = {
        id: 0,
        name: '',
        age: 0,
        salary: 0
    };
</script>

<div>
    <button on:click={async () => {
        const response = await fetch(`/data`, { method: 'GET' });
        
        let responseData = await response.text();

        console.log(responseData);
        data.value = responseData;
    }}>Get all data</button>

    <br>
    <br>

    <input bind:value={id} placeholder="id">
    <button on:click={async () => {
        const response = await fetch(`/data/${id}`, { method: 'GET' });

        let responseData = await response.text();

        console.log(responseData);
        data.value = responseData;
    }}>Get the data for this id</button>
    <button on:click={async () => {
        const response = await fetch(`/data/${id}`, {method: 'DELETE'});
        let responseData = await response.text();
        console.log(responseData);
        data.value = responseData;
    }}>Delete the data for this id</button>


    <br>
    <br>

    <input bind:value={newData.id} placeholder="id">
    <input bind:value={newData.name} placeholder="name">
    <input bind:value={newData.age} placeholder="age">
    <input bind:value={newData.salary} placeholder="salary">

    <button on:click={async () => {
        const response = await fetch(`/data/${newData.id}`, { 
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(newData)
        });
        
        let responseData = await response.text();
        console.log(responseData);
        data.value = responseData;
    }}>POST New Data</button>

    <button on:click={async () => {
        console.log(JSON.stringify(newData));
        const response = await fetch(`/data/${newData.id}`, { 
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(newData) 
        });
        
        let responseData = await response.text();
        console.log(responseData);
        data.value = responseData;
    }}>PUT New Data</button>

    <br>
    <br>


    <textarea bind:this={data}></textarea>
</div>