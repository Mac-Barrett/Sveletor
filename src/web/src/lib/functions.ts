export async function getSessionData(): Promise<any> {
    const response = await fetch('/session', { method: 'GET' });
    
    return await response.json();
}