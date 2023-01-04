<script lang="ts">
    export var link = ''
</script>

<!-- svelte-ignore a11y-click-events-have-key-events -->
<span on:click={() => window.location.href = link}><slot/></span>

<style>
    span {
        color: blue;
        text-decoration: underline;
        cursor: pointer;
    }
</style>